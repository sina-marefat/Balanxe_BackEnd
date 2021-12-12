package ir.balanxe.providers.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailUtil {

    private final EmailSender emailSender;

    @Autowired
    public EmailUtil(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(String email, String subject, String content) {
        emailSender.send(email, subject, content);
    }

    public String buildOTPEmail(String code) {
        return "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">\n" +
                "  <div style=\"margin:50px auto;width:70%;padding:20px 0\">\n" +
                "    <div style=\"border-bottom:1px solid #eee\">\n" +
                "      <a href=\"\" style=\"font-size:1.4em;color: #00466a;text-decoration:none;font-weight:600\">Your Brand</a>\n" +
                "    </div>\n" +
                "    <p style=\"font-size:1.1em\">Hi,</p>\n" +
                "    <p><big><big>تشکر بابت استفاده شما از بالانکس کد زیر را برای تایید اکانت خود استفاده کنید </big></big></p>\n" +
                "    <h2 style=\"background: #00466a;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">"+code+"</h2>\n" +
                "    <p style=\"font-size:0.9em;\">Regards,<br />Your Brand</p>\n" +
                "    <hr style=\"border:none;border-top:1px solid #eee\" />\n" +
                "    <div style=\"float:right;padding:8px 0;color:#aaa;font-size:0.8em;line-height:1;font-weight:300\">\n" +
                "      <p>Balanxe</p>\n" +
                "      <p>1600 Amphitheatre Parkway</p>\n" +
                "      <p>California</p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>";
    }

    public String buildTextEmail(String text) {
        return "";
    }

    public String buildRestorePasswordEmail(String link) {
        return "";
    }

    public String buildFailedLoginEmail() {
        return "";
    }

    public String buildSuccessfulLoginEmail() {
        return "";
    }
}
