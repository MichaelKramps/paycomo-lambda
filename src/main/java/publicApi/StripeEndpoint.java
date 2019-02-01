package publicApi;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import domain.PaycomoApiRequest;
import domain.PaycomoApiResponse;

import java.util.HashMap;
import java.util.Map;

// Ideally I'd like all the different business endpoints to extend this class
// That way I only need one set of tests to run
// Although this may work out better as an interface

public class StripeEndpoint implements RequestHandler<PaycomoApiRequest, PaycomoApiResponse> {
    private String privateApiKey;
    private PaycomoApiResponse response;

    public PaycomoApiResponse handleRequest(PaycomoApiRequest request, Context context) {
        setPrivateApiKey();
        Charge charge = chargeCard(request);
        if (charge == null){
            response = new PaycomoApiResponse(false, "There was a problem processing your payment.");
        } else {
            response = new PaycomoApiResponse(true, "Your payment was processed.");
        }
        return response;
    }

    private void setPrivateApiKey() {
        // this should be overridden in child classes
        // and set to the specific businesses private key
        this.privateApiKey = "sk_test_D6guK1T3Di9EmVWlcJ8JcLOg";
    }

    private Charge chargeCard(PaycomoApiRequest request){
        Stripe.apiKey = this.privateApiKey;

        Map<String, Object> params = new HashMap<>();
        params.put("amount", request.getAmount());
        params.put("currency", "usd");
        params.put("description", "Example charge");
        params.put("source", request.getPublicApiKey());

        try {
            Charge charge = Charge.create(params);
            return charge;
        } catch(Exception ignored){
            return null;
        }
    }

    public String getPrivateApiKey() {
        return privateApiKey;
    }
}
