package domain;

public class PaycomoApiResponse {
    private String message;

    public PaycomoApiResponse(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
