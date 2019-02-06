package publicApi;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import domain.PaycomoTransactionS3Request;

public class UploadToS3 implements RequestHandler<PaycomoTransactionS3Request, String> {
    public String handleRequest(PaycomoTransactionS3Request request, Context context) {
        String accessKeyId = System.getenv("S3_ACCESS_KEY");
        String secretKey = System.getenv("S3_SECRET_KEY");

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretKey);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        try {
            // Upload a text string as a new object.
            s3Client.putObject(request.getBucketName(), request.getDisplayName(), request.getContent());
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            return "error";
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            return "error 2";
        }
        return "success";
    }
}
