package domain;

public class PaycomoApiRequest {
    String message;

    public PaycomoApiRequest(String message){
        this.message = message;
    }

    public PaycomoApiRequest(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
