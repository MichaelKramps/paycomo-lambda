package publicApi;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import domain.PaycomoApiRequest;
import domain.PaycomoApiResponse;
import org.junit.Test;

public class StripeEndpointTest {
    @Test
    public void handleRequestNeverReturnsNull(){
        PaycomoApiRequest request = new PaycomoApiRequest();
        Context context = createContext();

        StripeEndpoint stripeEndpoint = new StripeEndpoint();

        assert(stripeEndpoint.handleRequest(request, context) != null);
    }

    @Test
    public void privateApiKeyIsSet(){
        PaycomoApiRequest request = new PaycomoApiRequest();
        Context context = createContext();

        StripeEndpoint stripeEndpoint = new StripeEndpoint();

        stripeEndpoint.handleRequest(request, context);

        assert(stripeEndpoint.getPrivateApiKey() != null);
    }

    @Test
    public void neverReturnsABlankResponse(){
        PaycomoApiRequest request = new PaycomoApiRequest();
        Context context = createContext();

        StripeEndpoint stripeEndpoint = new StripeEndpoint();

        PaycomoApiResponse response = stripeEndpoint.handleRequest(request, context);

        assert(response.getMessage() != null);
    }

    private Context createContext() {
        return new Context() {
            @Override
            public String getAwsRequestId() {
                return null;
            }

            @Override
            public String getLogGroupName() {
                return null;
            }

            @Override
            public String getLogStreamName() {
                return null;
            }

            @Override
            public String getFunctionName() {
                return null;
            }

            @Override
            public String getFunctionVersion() {
                return null;
            }

            @Override
            public String getInvokedFunctionArn() {
                return null;
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {
                return 0;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return null;
            }
        };
    }
}
