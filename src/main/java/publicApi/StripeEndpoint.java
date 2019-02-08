package publicApi;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import domain.PaycomoApiRequest;
import domain.PaycomoApiResponse;
import domain.PaycomoTransactionS3Request;

import java.util.HashMap;
import java.util.Map;

// Ideally I'd like all the different business endpoints to extend this class
// That way I only need one set of tests to run
// Although this may work out better as an interface

public class StripeEndpoint implements RequestHandler<PaycomoApiRequest, PaycomoApiResponse> {
    protected String privateApiKey;
    private PaycomoApiResponse response;

    public PaycomoApiResponse handleRequest(PaycomoApiRequest request, Context context) {
        setPrivateApiKey();
        Charge charge = chargeCard(request);
        if (charge == null){
            response = new PaycomoApiResponse(false, "There was a problem processing your payment.");
        } else {
            response = new PaycomoApiResponse(true, "Your payment was processed.");
        }
        publishToSnsTopic(createSnsRequest(charge));
        return response;
    }

    protected void setPrivateApiKey() {
        // this should be overridden in child classes
        // and set to the specific businesses private key
        this.privateApiKey = "";
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

    protected Map<String, Object> createChargeParameters(PaycomoApiRequest request){
        Map<String, Object> params = new HashMap<>();
        params.put("amount", request.getAmount());
        params.put("currency", "usd");
        params.put("description", "Default charge");
        params.put("source", request.getPublicApiKey());

        return params;
    }

    // Overwrite in child classes
    protected PaycomoTransactionS3Request createSnsRequest(Charge charge){
        return new PaycomoTransactionS3Request();
    }

    protected void publishToSnsTopic(PaycomoTransactionS3Request snsRequest){
        String accessKeyId = System.getenv("SNS_ACCESS_KEY");
        String secretKey = System.getenv("SNS_SECRET_KEY");

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretKey);
        //create a new SNS client and set endpoint
        AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.US_EAST_2)
                .build();

        String topicArn = "arn:aws:sns:us-east-2:718137051114:paycomo-transactions";

        //publish to an SNS topic
        String msg = "{" +
                "\"displayName\":\"" + snsRequest.getDisplayName() + "\"" +
                "\"bucketName\":\"" + snsRequest.getBucketName() + "\"" +
                "\"content\":\"" + snsRequest.getContent() + "\"" +
                "}";
        PublishRequest publishRequest = new PublishRequest(topicArn, msg);
        snsClient.publish(publishRequest);

        System.out.println(msg);
    }

    public String getPrivateApiKey() {
        return privateApiKey;
    }
}
