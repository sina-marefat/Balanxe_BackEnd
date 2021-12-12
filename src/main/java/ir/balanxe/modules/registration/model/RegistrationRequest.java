package ir.balanxe.modules.registration.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistrationRequest {

    private String email = "";
    private String mobile = "";
    private String password;


    public RegistrationRequest() {
    }

    public RegistrationRequest(String email, String mobile, String password) {
        this.email = email;
        this.mobile = mobile;
        this.password = password;
    }

    public String getEmail() {
        return email.toLowerCase();
    }

    public String getMobile() {
        return mobile;
    }

    public String getPassword() {
        return password;
    }

}
