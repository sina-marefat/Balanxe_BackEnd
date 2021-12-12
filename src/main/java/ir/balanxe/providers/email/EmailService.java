package ir.balanxe.providers.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService implements EmailSender {


    @Value("${spring.mail.username:temp}")
    String smtpUsername;

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void send(String email, String subject, String content) {
        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, "utf-8");
            helper.setText(content, true);
            helper.setFrom(smtpUsername);
            helper.setSubject(subject);
            helper.setTo(email);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }
}
