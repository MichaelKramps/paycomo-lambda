package domain;

public class PaycomoApiRequest {
    private String publicApiKey;
    private String cardToken;
    private float amount;

    public PaycomoApiRequest(String apiKey, float amount){
        this.publicApiKey = apiKey;
        this.amount = amount;
    }

    public PaycomoApiRequest(){}

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

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }
}
