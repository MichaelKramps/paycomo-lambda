package domain;

public class PaycomoTransactionS3Request {
    private String displayName;
    private String bucketName;
    private String content;

    public PaycomoTransactionS3Request(String displayName, String bucketName, String content){
        this.displayName = displayName;
        this.bucketName = bucketName;
        this.content = content;
    }

    public PaycomoTransactionS3Request(){}

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
