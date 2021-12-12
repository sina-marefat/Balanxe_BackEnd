package ir.balanxe.providers.kavenegar;

import com.kavenegar.sdk.KavenegarApi;
import ir.balanxe.providers.sms.SmsSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class KavenegarService implements SmsSender {


    private static KavenegarApi kavenegarApi;
    private static final String SMS_VERIFY_CODE_TEMPLATE_NAME = "verify-balanxe";
    private static final String RESTORE_PASSWORD_TEMPLATE_NAME = "restore-password-balanxe";

    public KavenegarService(@Value("${kavenegar.api.key:temp}") String KAVENEGAR_API_KEY) {
        if (kavenegarApi == null)
            kavenegarApi = new KavenegarApi(KAVENEGAR_API_KEY);
    }



    @Override
    @Async
    public void sendVerifyCode(String mobile, String code) {
        kavenegarApi.verifyLookup(mobile, code, SMS_VERIFY_CODE_TEMPLATE_NAME);
    }

    @Override
    @Async
    public void sendRestorePasswordLink(String mobile, String link) {
        kavenegarApi.verifyLookup(mobile, link, RESTORE_PASSWORD_TEMPLATE_NAME);
    }



}
