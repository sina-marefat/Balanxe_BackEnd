package ir.balanxe.modules.registration.entities.otp.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ConfirmOTPRequest {

    private int code;

    @JsonProperty(value = "user_id")
    private UUID UserId;

    public ConfirmOTPRequest() {
    }

    public ConfirmOTPRequest(int code, UUID userId) {
        this.code = code;
        UserId = userId;
    }

    public int getCode() {
        return code;
    }

    public UUID getUserId() {
        return UserId;
    }

}
