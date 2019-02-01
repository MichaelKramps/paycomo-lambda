package domain;

public class PaycomoApiResponse {
    boolean success;
    String message;

    public PaycomoApiResponse(boolean success, String message){
        this.success = success;
        this.message = message;
    }

    public PaycomoApiResponse(){}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
