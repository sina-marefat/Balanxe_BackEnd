package ir.balanxe.modules.registration.entities.refreshtoken.repository;

import ir.balanxe.modules.registration.entities.refreshtoken.model.BlackListRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRefreshTokenRepository extends JpaRepository<BlackListRefreshToken, Long> {

    boolean existsBlackListRefreshTokenByTokenId(String tokenId);
}
