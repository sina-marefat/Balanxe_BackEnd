package ir.balanxe.modules.registration.entities.otp.repository;

import ir.balanxe.modules.registration.entities.otp.model.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {

    Optional<ConfirmationCode> findByCodeAndUserId(int code, UUID userId);

    Optional<ConfirmationCode> findByUserId(UUID userId);

}
