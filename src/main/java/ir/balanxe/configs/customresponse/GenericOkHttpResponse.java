package ir.balanxe.configs.customresponse;

public class GenericOkHttpResponse {
    private String message;

    public GenericOkHttpResponse() {
    }

    public GenericOkHttpResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
