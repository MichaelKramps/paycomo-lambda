package publicApi;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.stripe.model.Charge;
import domain.PaycomoApiRequest;
import domain.PaycomoApiResponse;
import domain.PaycomoTransactionS3Request;

import java.util.HashMap;
import java.util.Map;

public class PersonalEndpoint extends StripeEndpoint {
    @Override
    protected void setPrivateApiKey() {
        this.privateApiKey = "sk_test_D6guK1T3Di9EmVWlcJ8JcLOg";
    }

    @Override
    protected Map<String, Object> createChargeParameters(PaycomoApiRequest request){
        Map<String, Object> params = new HashMap<>();
        params.put("amount", request.getAmount());
        params.put("currency", "usd");
        params.put("description", "Charge credited to my personal Stripe account");
        params.put("source", request.getCardToken());

        return params;
    }

    @Override
    protected PaycomoTransactionS3Request createSnsRequest(Charge charge){
        PaycomoTransactionS3Request request = new PaycomoTransactionS3Request();
        request.setBucketName("paycomo-transactions");
        request.setDisplayName("yo-yo-test");
        request.setContent("Hello, There, Mr, Robinson");

        return request;
    }
}
