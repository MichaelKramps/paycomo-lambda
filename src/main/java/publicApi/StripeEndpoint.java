package publicApi;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import domain.PaycomoApiRequest;
import domain.PaycomoApiResponse;

import javax.ws.rs.client.Client;

// Ideally I'd like all the different business endpoints to extend this class
// That way I only need one set of tests to run

public class StripeEndpoint implements RequestHandler<PaycomoApiRequest, PaycomoApiResponse> {
    private Client client;

    public PaycomoApiResponse handleRequest(PaycomoApiRequest request, Context context) {
        return new PaycomoApiResponse();
    }
}
