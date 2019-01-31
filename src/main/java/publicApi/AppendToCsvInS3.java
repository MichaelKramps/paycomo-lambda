package publicApi;

import com.amazonaws.services.lambda.runtime.Context;

public class AppendToCsvInS3 {
    public String handleRequest(String request, Context context) {
        return new String(request);
    }
}
