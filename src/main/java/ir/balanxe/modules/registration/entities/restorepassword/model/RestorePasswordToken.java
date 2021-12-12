package ir.balanxe.modules.registration.entities.restorepassword.model;

import ir.balanxe.modules.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "restore_passwords")
public class RestorePasswordToken {

    @SequenceGenerator(
            name = "restore_password_sequence",
            sequenceName = "restore_password_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "restore_password_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public RestorePasswordToken() {
    }

    public RestorePasswordToken(String token, LocalDateTime createdAt, LocalDateTime expiredAt, User user) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public User getUser() {
        return user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
