package ir.balanxe.modules.registration.entities.refreshtoken.model;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "black_list_refresh_tokens")
public class BlackListRefreshToken {

    @SequenceGenerator(
            name = "black_list_refresh_token_sequence",
            sequenceName = "black_list_refresh_token_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "black_list_refresh_token_sequence"
    )
    private Long id;

    private String tokenId;

    @Column(name = "expires_at")
    private Date expiresAt;

    public BlackListRefreshToken() {
    }

    public BlackListRefreshToken(String tokenId, Date expiresAt) {
        this.tokenId = tokenId;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public String getTokenId() {
        return tokenId;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }
}
