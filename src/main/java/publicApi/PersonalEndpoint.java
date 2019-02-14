package publicApi;

import com.stripe.model.Charge;
import domain.PaycomoApiRequest;
import domain.PaycomoTransactionS3Request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PersonalEndpoint extends StripeEndpoint {
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
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyy-mm-dd_hh:mm:ss:SSS");
            request.setDisplayName(dateFormat.parse(new Date().toString()).toString());
        } catch (ParseException p){
            request.setDisplayName("dateParseError");
        }
        request.setContent("PersonalEndpoint," + charge.getAmount());

        return request;
    }
}
