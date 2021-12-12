package ir.balanxe.providers.email;

public interface EmailSender {
    void send(String email, String subject, String content);
}
