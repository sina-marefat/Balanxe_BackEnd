package ir.balanxe.providers.sms;

import com.kavenegar.sdk.excepctions.ApiException;
import com.kavenegar.sdk.excepctions.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SmsUtil {

    private final SmsSender smsSender;

    @Autowired
    public SmsUtil(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    public void sendCodeForMobile(String mobile, String code) {
        try {
            smsSender.sendVerifyCode(mobile, code);
        } catch (HttpException | ApiException e) {
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "KaveNegar Exception" + e);
        }
    }

    public void sendRestorePasswordLink(String mobile, String link) {
        smsSender.sendRestorePasswordLink(mobile, link);
    }

}
