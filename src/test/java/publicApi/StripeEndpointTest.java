package publicApi;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import domain.PaycomoApiRequest;
import domain.PaycomoApiResponse;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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

    @Test
    public void listS3Objects(){
        String accessKeyId = "AKIAJJLAJNWY3WUYQHKA";
        String secretKey = "";
        String bucketName = "paycomo-transactions";
        String stringObjKeyName = "test-string";
        String fileObjKeyName = "test-txt.txt";
        String filePath = "http://s3.amazonaws.com/" + bucketName + "/" + fileObjKeyName;

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretKey);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os: objects) {
            System.out.println("* " + os.getKey());
        }
    }

    @Test
    public void writeToS3Trial(){
        String accessKeyId = "AKIAJJLAJNWY3WUYQHKA";
        String secretKey = "";
        String bucketName = "paycomo-transactions";
        String stringObjKeyName = "test-string-6";
        String fileObjKeyName = "test-txt.txt";
        String filePath = "http://s3.amazonaws.com/" + bucketName + "/" + fileObjKeyName;
        //String filePath = "https://s3.us-east-2.amazonaws.com/" + bucketName;

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretKey);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        try {
            // Upload a text string as a new object.
            s3Client.putObject(bucketName, stringObjKeyName, "Uploaded Eleanor String Object");
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            System.out.println("ase exception");
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            System.out.println("sdk client exception");
            e.printStackTrace();
        }
    }

    @Test
    public void readS3ObjectTrial(){
        String accessKeyId = "AKIAJJLAJNWY3WUYQHKA";
        String secretKey = "";
        String bucketName = "paycomo-transactions";
        String stringObjKeyName = "test-string-6";

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretKey);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        try {
            System.out.println("Downloading an object");
            S3Object fullObject = s3Client.getObject(new GetObjectRequest(bucketName, stringObjKeyName));
            System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
            System.out.println("Content: ");
            displayTextInputStream(fullObject.getObjectContent());
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            System.out.println("ase exception");
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            System.out.println("sdk client exception");
            e.printStackTrace();
        }
        catch(IOException e){
            System.out.println("io exception");
            e.printStackTrace();
        }
    }

    private static void displayTextInputStream(InputStream input) throws IOException {
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println();
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
