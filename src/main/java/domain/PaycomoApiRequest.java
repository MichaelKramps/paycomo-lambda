package domain;

public class PaycomoApiRequest {
    private String publicApiKey;
    private float amount;

    public PaycomoApiRequest(String apiKey, float amount){
        this.publicApiKey = apiKey;
        this.amount = amount;
    }

    public PaycomoApiRequest(){}

    public float getAmount() {
        return amount;
    }

    public String getPublicApiKey() {
        return publicApiKey;
    }
}
