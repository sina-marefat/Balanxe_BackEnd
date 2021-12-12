package ir.balanxe.modules.registration.entities.restorepassword.request;

public class ResetPasswordRequest {

    private String token;
    private String password;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String token, String password) {
        this.token = token;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }
}
