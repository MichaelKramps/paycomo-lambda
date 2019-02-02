package publicApi;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import domain.PaycomoApiRequest;
import domain.PaycomoApiResponse;

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
}
