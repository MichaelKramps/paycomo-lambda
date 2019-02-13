package publicApi;

import com.amazonaws.services.lambda.runtime.*;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import domain.PaycomoApiRequest;
import domain.PaycomoApiResponse;
import domain.PaycomoTransactionS3Request;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import java.util.Map;

abstract public class StripeEndpoint implements RequestHandler<APIGatewayProxyRequestEvent, PaycomoApiResponse> {
    private PaycomoApiResponse response;

    public PaycomoApiResponse handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String requestBody = request.getBody();
        Charge charge = chargeCard(requestJson);
        if (charge == null){
            response = new PaycomoApiResponse(false, "There was a problem processing your payment.");
        } else {
            response = new PaycomoApiResponse(true, "Your payment was processed.");
        }
        publishToSnsTopic(createSnsRequest(charge));
        return response;
    }

    protected Charge chargeCard(PaycomoApiRequest request){
        Stripe.apiKey = System.getenv("STRIPE_PRIVATE_KEY");

        Map<String, Object> params = createChargeParameters(request);

        try {
            Charge charge = Charge.create(params);
            return charge;
        } catch(Exception ignored){
            return null;
        }
    }

    abstract protected Map<String, Object> createChargeParameters(PaycomoApiRequest request);

    abstract protected PaycomoTransactionS3Request createSnsRequest(Charge charge);

    protected void publishToSnsTopic(PaycomoTransactionS3Request snsRequest){
        String accessKeyId = System.getenv("SNS_ACCESS_KEY");
        String secretKey = System.getenv("SNS_SECRET_KEY");

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);
        //create a new SNS client and set endpoint
        AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                .withCredentials(StaticCredentialsProvider.create(awsCredentials))
                .withRegion(Region.US_EAST_2)
                .build();

        String topicArn = "arn:aws:sns:us-east-2:718137051114:paycomo-transactions";

        //publish to an SNS topic
        String msg = "{" +
                "\"displayName\":\"" + snsRequest.getDisplayName() + "\"," +
                "\"bucketName\":\"" + snsRequest.getBucketName() + "\"," +
                "\"content\":\"" + snsRequest.getContent() + "\"" +
                "}";
        PublishRequest publishRequest = new PublishRequest(topicArn, msg);
        snsClient.publish(publishRequest);

        System.out.println(msg);
    }
}
