package ir.balanxe.modules.registration.service;


import ir.balanxe.configs.customresponse.GenericOkHttpResponse;
import ir.balanxe.modules.log.model.Log;
import ir.balanxe.modules.registration.entities.otp.model.ConfirmationCode;
import ir.balanxe.modules.registration.entities.otp.request.ConfirmOTPRequest;
import ir.balanxe.modules.registration.entities.otp.service.ConfirmationCodeService;
import ir.balanxe.modules.registration.entities.refreshtoken.service.BlackListRefreshTokenService;
import ir.balanxe.modules.registration.entities.restorepassword.model.RestorePasswordToken;
import ir.balanxe.modules.registration.entities.restorepassword.request.ResetPasswordRequest;
import ir.balanxe.modules.registration.entities.restorepassword.service.RestorePasswordTokenService;
import ir.balanxe.modules.registration.model.LoginRequest;
import ir.balanxe.modules.registration.model.RegistrationRequest;
import ir.balanxe.modules.user.entities.personalinfo.model.PersonalUserInformation;
import ir.balanxe.modules.user.entities.personalinfo.service.PersonalUserInformationService;
import ir.balanxe.modules.user.entities.role.service.UserRoleService;
import ir.balanxe.modules.user.model.User;
import ir.balanxe.modules.user.service.UserService;
import ir.balanxe.providers.email.EmailUtil;
import ir.balanxe.providers.jwt.JwtResponse;
import ir.balanxe.providers.jwt.JwtUtil;
import ir.balanxe.providers.message.MessageUtil;
import ir.balanxe.providers.sms.SmsUtil;
import ir.balanxe.providers.validator.EmailValidator;
import ir.balanxe.providers.validator.MobileValidator;
import ir.balanxe.providers.validator.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua_parser.Client;
import ua_parser.Parser;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationService {

    private final PersonalUserInformationService personalUserInformationService;
    private final BlackListRefreshTokenService blackListRefreshTokenService;
    private final RestorePasswordTokenService restorePasswordTokenService;
    private final ConfirmationCodeService confirmationCodeService;
    private final PasswordValidator passwordValidator;
    private final MobileValidator mobileValidator;
    private final UserRoleService userRoleService;
    private final EmailValidator emailValidator;
    private final UserService userService;
    private final EmailUtil emailUtil;
    private final SmsUtil smsUtil;
    private final JwtUtil jwtUtil;

    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    public RegistrationService(
            PersonalUserInformationService personalUserInformationService,
            EmailValidator emailValidator,
            MobileValidator mobileValidator,
            UserService userService,
            UserRoleService userRoleService,
            ConfirmationCodeService confirmationCodeService,
            BlackListRefreshTokenService blackListRefreshTokenService,
            RestorePasswordTokenService restorePasswordTokenService,
            PasswordValidator passwordValidator,
            EmailUtil emailUtil,
            SmsUtil smsUtil,
            JwtUtil jwtUtil
    ) {
        this.personalUserInformationService=personalUserInformationService;
        this.emailValidator = emailValidator;
        this.mobileValidator = mobileValidator;
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.confirmationCodeService = confirmationCodeService;
        this.blackListRefreshTokenService = blackListRefreshTokenService;
        this.restorePasswordTokenService = restorePasswordTokenService;
        this.passwordValidator = passwordValidator;
        this.emailUtil = emailUtil;
        this.smsUtil = smsUtil;
        this.jwtUtil = jwtUtil;
    }


    public User register(
            RegistrationRequest request
    ) {
        if (request.getEmail().isEmpty())
            return registerByMobile(request);
        else
            return registerByEmail(request);
    }

    private User registerByEmail(
            RegistrationRequest request
    ) {
        String email = request.getEmail();
        String code;
        User user;

        if (!emailValidator.test(email))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageUtil.getDesiredMessage("invalid.email.msg"));

        else if (!passwordValidator.test(request.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageUtil.getDesiredMessage("invalid.password.msg"));

        if (userService.findByEmail(email).isPresent()) {
            user = userService.findByEmail(email).get();
            user.setPassword(request.getPassword());
            code = userService.registerUser(user);
        } else {
            user = new User(email, null, email, request.getPassword(),userRoleService.findRoleById(1L));
            code = userService.registerUser(user);
            user = userService.findByUsername(user.getUsername()).get();
        }
        emailUtil.sendEmail(email, MessageUtil.getDesiredMessage("verification.email.subject"), emailUtil.buildOTPEmail(code));
        return user;
    }

    private User registerByMobile(
            RegistrationRequest request
    ) {
        String mobile = request.getMobile();
        String code;
        User user;


        if (!mobileValidator.test(mobile))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageUtil.getDesiredMessage("invalid.phone.number.msg"));

        else if (!passwordValidator.test(request.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageUtil.getDesiredMessage("invalid.password.msg"));

        if (userService.findByMobile(mobile).isPresent()) {
            user = userService.findByMobile(mobile).get();
            user.setPassword(request.getPassword());
            code = userService.registerUser(user);
        } else {
            user = new User(null, mobile, mobile, request.getPassword(), userRoleService.findRoleById(1L));
            code = userService.registerUser(user);
            user = userService.findByUsername(user.getUsername()).get();
        }

        smsUtil.sendCodeForMobile(mobile, code);
        return user;
    }

    public User renewOTP(
            UUID userId
    ) {
        String code;
        User user = userService.findById(userId);

        if (user.isEnabled())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageUtil.getDesiredMessage("user.already.exists.msg"));

        if (user.getMobile() != null && mobileValidator.test(user.getMobile())) {

            code = userService.createOtp(user, true);
            smsUtil.sendCodeForMobile(user.getMobile(), code);

        } else if (user.getEmail() != null && emailValidator.test(user.getEmail())) {

            code = userService.createOtp(user, true);
            emailUtil.sendEmail(user.getEmail(), MessageUtil.getDesiredMessage("verification.email.subject"), emailUtil.buildOTPEmail(code));

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    MessageUtil.getDesiredMessage("invalid.properties.msg"));

        return user;
    }

    public JwtResponse confirmOTP(
            ConfirmOTPRequest request
    ) {

        ConfirmationCode confirmationCode = confirmationCodeService.findConfirmationCodeByOTPAndUser(
                request.getCode(),
                request.getUserId()
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                MessageUtil.getDesiredMessage("incorrect.code.msg")));

        if (confirmationCode.getConfirmedAt() != null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageUtil.getDesiredMessage("user.already.exists.msg"));

        if (confirmationCode.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageUtil.getDesiredMessage("expired.code.msg"));

        User user = confirmationCode.getUser();

        confirmationCodeService.setConfirmedAt(confirmationCode);
        userService.enableUser(user);

        Log log = new Log("confirm OTP", user.getId());
        logger.info(log.toString());
        return createJwtToken(user);
    }

    public JwtResponse login(
            HttpServletRequest request,
            LoginRequest loginRequest
    ) {
        User user;

        if (!loginRequest.getEmail().isEmpty())
            user = userService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"fake"));
        else
            user = userService.findByMobile(loginRequest.getMobile())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                            MessageUtil.getDesiredMessage("incorrect.credentials.msg")));

        System.out.println("HERE");
        if (!user.isEnabled())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    MessageUtil.getDesiredMessage("not.confirmed.account.msg"));

        Parser parser = new Parser();
        Client ca = parser.parse(request.getHeader("User-Agent"));

        if (!userService.checkEqualPasswords(loginRequest.getPassword(), user.getPassword())) {
//            if (user.getEmail() != null)
//                emailUtil.sendEmail(user.getEmail(), emailUtil.buildFailedLoginEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    MessageUtil.getDesiredMessage("incorrect.credentials.msg"));
        }


        Log log = new Log("login", user.getId());
        logger.info(log.toString());

        return createJwtToken(user);
    }

    public JwtResponse confirmRefreshToken(
            String refreshToken
    ) {

        if (!jwtUtil.extractGrantType(refreshToken).equals("refresh_token"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, MessageUtil.getDesiredMessage("invalid.refresh.token.msg"));

        String tokenId = jwtUtil.extractTokenId(refreshToken);

        boolean tokenIsBlackList = blackListRefreshTokenService.searchInBlackList(tokenId);
        Date date = jwtUtil.extractExpiration(refreshToken);

        if (tokenIsBlackList || date.before(new Date()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, MessageUtil.getDesiredMessage("invalid.refresh.token.msg"));

        blackListRefreshTokenService.saveBlackListRefreshToken(tokenId, jwtUtil.extractExpiration(refreshToken));
        logger.info("user refreshed token");
        return createJwtToken(userService.findById(jwtUtil.extractUserId(refreshToken)));
    }

    public ResponseEntity<?> forgetPassword(
            String email,
            String mobile
    ) {
        final User[] user = new User[1];

        String resetPasswordUrl = "https://balanxe.ir/auth/reset-password?token=";

        userService.findByEmailOrMobile(email,mobile).ifPresent(user1 -> {
            if(!user1.isEnabled()){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        MessageUtil.getDesiredMessage("not.confirmed.account.msg"));
            } else {
                user[0] =user1;
            }
        });

        if (email != null) {

            if (!emailValidator.test(email))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        MessageUtil.getDesiredMessage("invalid.email.msg"));


            String token = createRestoreToken(user[0]);
            String link = resetPasswordUrl + token;
            emailUtil.sendEmail(email, MessageUtil.getDesiredMessage("restore.password.subject"), emailUtil.buildRestorePasswordEmail(link));

        } else {
            if (!mobileValidator.test(mobile))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        MessageUtil.getDesiredMessage("invalid.phone.number.msg"));


            String token = createRestoreToken(user[0]);
            String link = resetPasswordUrl + token;

            smsUtil.sendRestorePasswordLink(mobile, link);
        }


        return ResponseEntity.ok(new GenericOkHttpResponse(MessageUtil.getDesiredMessage("restore.email.sent")));
    }

    public void checkRestoreToken(
            String token
    ) {
        RestorePasswordToken restorePasswordToken = restorePasswordTokenService.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        MessageUtil.getDesiredMessage("invalid.restore.token.msg")));

        if (restorePasswordToken.getConfirmedAt() != null || restorePasswordToken.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    MessageUtil.getDesiredMessage("invalid.restore.token.msg"));
    }

    public String resetPassword(
            ResetPasswordRequest request
    ) {

        if (!passwordValidator.test(request.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageUtil.getDesiredMessage("invalid.password.msg"));

        RestorePasswordToken restorePasswordToken = restorePasswordTokenService.findByToken(request.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        MessageUtil.getDesiredMessage("invalid.restore.token.msg")));

        if (restorePasswordToken.getConfirmedAt() != null || restorePasswordToken.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    MessageUtil.getDesiredMessage("invalid.restore.token.msg"));

        restorePasswordToken.setConfirmedAt(LocalDateTime.now());
        restorePasswordTokenService.saveRestorePasswordToken(restorePasswordToken);
        userService.updatePassword(restorePasswordToken.getUser(), request.getPassword());

        logger.info(new Log("reset password", restorePasswordToken.getUser().getId()).toString());
        return "Confirmed";
    }

    private JwtResponse createJwtToken(
            User user
    ) {

        String accessToken = jwtUtil.generateToken(user.getId().toString(), false);
        String refreshToken = jwtUtil.generateToken(user.getId().toString(), true);
        Optional<PersonalUserInformation> userInformation = personalUserInformationService.getPersonalUserInfoByUserId(user.getId());

        int accessTokenExpiresIn = 5;
        int refreshTokenExpiresIn = 10080;

        return new JwtResponse(
                "Bearer",
                String.valueOf(accessTokenExpiresIn),
                String.valueOf(refreshTokenExpiresIn),
                accessToken,
                refreshToken,
                user.getId(),
                userInformation.map(PersonalUserInformation::getFirstName).orElse(null),
                userInformation.map(PersonalUserInformation::getLastName).orElse(null)
        );
    }

    private String createRestoreToken(
            User user
    ) {
        RestorePasswordToken restorePasswordToken;
        String token = UUID.randomUUID().toString();

        if (restorePasswordTokenService.findTokenByUserId(user.getId()).isPresent()) {
            restorePasswordToken = restorePasswordTokenService.findTokenByUserId(user.getId()).get();

            if (restorePasswordToken.getCreatedAt().plusMinutes(2).isAfter(LocalDateTime.now()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MessageUtil.getDesiredMessage("two.minutes.waiting.msg"));

            restorePasswordToken.setCreatedAt(LocalDateTime.now());
            restorePasswordToken.setExpiredAt(LocalDateTime.now().plusMinutes(15));
            restorePasswordToken.setConfirmedAt(null);
            restorePasswordToken.setToken(token);
        } else {
            restorePasswordToken = new RestorePasswordToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    user
            );
        }

        restorePasswordTokenService.saveRestorePasswordToken(restorePasswordToken);
        return token;
    }

}
