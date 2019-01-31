package domain;

public class PaycomoApiRequest {
    String publicApiKey;
    float amount;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getPublicApiKey() {
        return publicApiKey;
    }

    public void setPublicApiKey(String publicApiKey) {
        this.publicApiKey = publicApiKey;
    }

    public PaycomoApiRequest(String apiKey, float amount){
        this.publicApiKey = apiKey;
        this.amount = amount;
    }

    public PaycomoApiRequest(){}
}
