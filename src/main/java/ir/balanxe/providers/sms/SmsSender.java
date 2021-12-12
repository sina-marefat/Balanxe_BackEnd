package ir.balanxe.providers.sms;


public interface SmsSender {

    void sendVerifyCode(String mobile, String code);

    void sendRestorePasswordLink(String mobile, String link);

}
