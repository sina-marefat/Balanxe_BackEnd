package ir.balanxe.modules.registration.entities.restorepassword.repository;

import ir.balanxe.modules.registration.entities.restorepassword.model.RestorePasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestorePasswordTokenRepository extends JpaRepository<RestorePasswordToken, Long> {

    Optional<RestorePasswordToken> findByUserId(UUID userId);

    Optional<RestorePasswordToken> findByToken(String token);
    
    boolean existsByUserIdAndConfirmedAtAfter(UUID userId, LocalDateTime yesterdayTime);
}
