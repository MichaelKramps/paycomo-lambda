package internal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import domain.PaycomoApiRequest;
import domain.PaycomoApiResponse;

public class personalEndpoint implements RequestHandler<PaycomoApiRequest, PaycomoApiResponse> {
    public PaycomoApiResponse handleRequest(PaycomoApiRequest request, Context context) {
        return new PaycomoApiResponse(request.getMessage());
    }
}
