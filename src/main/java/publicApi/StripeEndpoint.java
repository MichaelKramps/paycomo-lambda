package publicApi;

import com.amazonaws.services.lambda.runtime.*;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
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
        PaycomoApiRequest chargeRequest = getRequestBody(request);
        Charge charge = chargeCard(chargeRequest);
        if (charge == null){
            response = new PaycomoApiResponse(false, "There was a problem processing your payment.");
        } else {
            response = new PaycomoApiResponse(true, "Your payment was processed.");
        }
        publishToSnsTopic(createSnsRequest(charge));
        return response;
    }

    private PaycomoApiRequest getRequestBody(APIGatewayProxyRequestEvent request) {
        ObjectMapper mapper = new ObjectMapper();
        PaycomoApiRequest requestBody;
        try{
            requestBody = mapper.readValue(request.getBody(), PaycomoApiRequest.class);
        } catch(Exception e){
            requestBody = new PaycomoApiRequest();
        }
        return requestBody;
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
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

        String topicArn = "arn:aws:sns:us-east-2:718137051114:paycomo-transactions";

        //publish to an SNS topic
        String msg = "{" +
                "\"displayName\":\"" + snsRequest.getDisplayName() + "\"," +
                "\"bucketName\":\"" + snsRequest.getBucketName() + "\"," +
                "\"content\":\"" + snsRequest.getContent() + "\"" +
                "}";
        PublishRequest publishRequest = PublishRequest.builder().topicArn(topicArn).message(msg).build();
        snsClient.publish(publishRequest);

        System.out.println(msg);
    }
}
