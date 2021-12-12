package ir.balanxe.modules.registration.entities.otp.service;

import ir.balanxe.modules.registration.entities.otp.model.ConfirmationCode;
import ir.balanxe.modules.registration.entities.otp.repository.ConfirmationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConfirmationCodeService {

    private final ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    public ConfirmationCodeService(ConfirmationCodeRepository confirmationCodeRepository) {
        this.confirmationCodeRepository = confirmationCodeRepository;
    }


    public void saveConfirmationCode(ConfirmationCode confirmationCode) {
        confirmationCodeRepository.save(confirmationCode);
    }

    public Optional<ConfirmationCode> findConfirmationCodeByOTPAndUser(int code, UUID userId) {
        return confirmationCodeRepository.findByCodeAndUserId(code, userId);
    }

    public Optional<ConfirmationCode> findConfirmationCodeByUserId(UUID userId) {
        return confirmationCodeRepository.findByUserId(userId);
    }

    public void setConfirmedAt(ConfirmationCode confirmationCode) {
        confirmationCode.setConfirmedAt(LocalDateTime.now());
        confirmationCodeRepository.save(confirmationCode);
    }

    public void deleteUnusableOTPCodes() {
        List<ConfirmationCode> confirmationCodeList =
                confirmationCodeRepository.findAll()
                        .stream()
                        .filter(confirmationCode -> confirmationCode.getConfirmedAt() != null ||
                                (confirmationCode.getConfirmedAt() == null &&
                                        confirmationCode.getExpiresAt().isBefore(LocalDateTime.now())))
                        .collect(Collectors.toList());

        confirmationCodeRepository.deleteAll(confirmationCodeList);
    }

}
