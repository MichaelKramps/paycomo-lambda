package publicApi;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import software.amazon.awssdk.services.s3.S3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.PaycomoTransactionS3Request;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SSS");
            s3Request.setDisplayName(dateFormat.format(new Date()));
        }

        System.out.println("bucketName: " + s3Request.getBucketName());
        System.out.println(request.toString());

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretKey);

        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Request.getBucketName())
                .key(s3Request.getDisplayName())
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromString(s3Request.getContent()));
        }
        catch(Exception e) {
            System.out.println("Amazon Service Exception: ");
            e.printStackTrace();
            return "Amazon Service Exception";
        }
        return "Success";
    }
}
