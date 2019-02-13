package publicApi;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import software.amazon.awssdk.services.s3.S3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.PaycomoTransactionS3Request;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadToS3 implements RequestHandler<SNSEvent, String> {
    public String handleRequest(SNSEvent request, Context context) {
        String accessKeyId = System.getenv("S3_ACCESS_KEY");
        String secretKey = System.getenv("S3_SECRET_KEY");

        SNSEvent.SNS sns = request.getRecords().get(0).getSNS();
        ObjectMapper mapper = new ObjectMapper();
        PaycomoTransactionS3Request s3Request;
        try {
            s3Request = mapper.readValue(sns.getMessage(), PaycomoTransactionS3Request.class);
        } catch (Exception e){
            s3Request = new PaycomoTransactionS3Request();
            s3Request.setBucketName("paycomo-transactions");
            s3Request.setContent("There was a problem parsing the SNS object");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyy-mm-dd_hh:mm:ss:SSS");
            try{
                s3Request.setDisplayName("s3error-" + dateFormat.parse(new Date().toString()));
            } catch (ParseException p){
                s3Request.setDisplayName("dateParseError");
                s3Request.setContent("There was a problem parsing the SNS object and parsing the Date");
            }
        }

        System.out.println("bucketName: " + s3Request.getBucketName());
        System.out.println(request.toString());

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretKey);

        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        try {
            // Upload a text string as a new object.

            s3Client.putObject(s3Request.getBucketName(), s3Request.getDisplayName(), s3Request.getContent());
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            System.out.println("Amazon Service Exception: ");
            e.printStackTrace();
            return "Amazon Service Exception";
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            System.out.println("SDK Client Exception: ");
            e.printStackTrace();
            return "SDK Client Exception";
        }
        return "Success";
    }
}
