package ir.balanxe.modules.log.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(value = "logs")
public class Log {

    @Id
    private UUID id;
    private String message;
    private UUID userId;

    @Field(name = "created_at")
    private LocalDateTime createdAt;

    public Log() {
    }

    public Log(String message, UUID userId) {
        this.id = UUID.randomUUID();
        this.message = message;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                " message: " + message + ", " +
                " user_id: " + userId + ", " +
                " created_at: " + createdAt +
                "}";
    }
}
