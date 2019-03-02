package domain;

public class PaycomoApiRequest {
    private String publicApiKey;
    private String cardToken;
    private String email;
    private int amount;

    public PaycomoApiRequest(String apiKey, int amount){
        this.publicApiKey = apiKey;
        this.amount = amount;
    }

    public PaycomoApiRequest(){}

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
