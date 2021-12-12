package ir.balanxe.modules.registration.controller;

import ir.balanxe.modules.registration.entities.otp.request.ConfirmOTPRequest;
import ir.balanxe.modules.registration.entities.restorepassword.request.ResetPasswordRequest;
import ir.balanxe.modules.registration.model.LoginRequest;
import ir.balanxe.modules.registration.model.RegistrationRequest;
import ir.balanxe.modules.registration.service.RegistrationService;
import ir.balanxe.modules.user.model.User;
import ir.balanxe.providers.jwt.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(
            RegistrationService registrationService
    ) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public User register(
            @RequestBody RegistrationRequest request
    ) {
        return registrationService.register(request);
    }

    @PostMapping("/renew")
    public User renewOTP(
            @Nullable @RequestParam("userId") UUID userId
    ) {
        return registrationService.renewOTP(userId);
    }

    @PostMapping("/confirm")
    public JwtResponse confirmCode(
            @RequestBody ConfirmOTPRequest request
    ) {
        return registrationService.confirmOTP(request);
    }

    @PostMapping("/login")
    public JwtResponse login(
            HttpServletRequest request,
            @RequestBody LoginRequest loginRequest
    ) {
        return registrationService.login(request, loginRequest);
    }

    @GetMapping("/refresh")
    public JwtResponse refreshJWT(
            @RequestParam("refresh_token") String refreshToken
    ) {
        return registrationService.confirmRefreshToken(refreshToken);
    }

    @GetMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(
            @Nullable @RequestParam("email") String email,
            @Nullable @RequestParam("mobile") String mobile
    ) {
        return registrationService.forgetPassword(email, mobile);
    }

    @GetMapping("/check-restore-password-token")
    public void checkRestorePasswordToken(
            @RequestParam("token") String token
    ) {
        registrationService.checkRestoreToken(token);
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {
        return registrationService.resetPassword(request);
    }

    @GetMapping("/test")
    public String test(@RequestParam("test") String test) {
        if(test.equals("er")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"BAD");

        }
        return "ok";
    }

}
