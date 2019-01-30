package domain;

public class PaycomoApiResponse {
    String message;

    public PaycomoApiResponse(String message){
        this.message = message;
    }

    public PaycomoApiResponse(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
