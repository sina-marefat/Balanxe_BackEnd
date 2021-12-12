package ir.balanxe.modules.registration.entities.restorepassword.service;

import ir.balanxe.modules.registration.entities.restorepassword.model.RestorePasswordToken;
import ir.balanxe.modules.registration.entities.restorepassword.repository.RestorePasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RestorePasswordTokenService {

    private final RestorePasswordTokenRepository restorePasswordTokenRepository;

    @Autowired
    public RestorePasswordTokenService(RestorePasswordTokenRepository restorePasswordTokenRepository) {
        this.restorePasswordTokenRepository = restorePasswordTokenRepository;
    }

    public Optional<RestorePasswordToken> findTokenByUserId(UUID userId) {
        return restorePasswordTokenRepository.findByUserId(userId);
    }

    public void saveRestorePasswordToken(RestorePasswordToken restorePasswordToken) {
        restorePasswordTokenRepository.save(restorePasswordToken);
    }

    public boolean existsRestorePasswordTokenForWithdraw(UUID userId) {
        return restorePasswordTokenRepository.existsByUserIdAndConfirmedAtAfter(userId, LocalDateTime.now().minusDays(1));
    }

    public Optional<RestorePasswordToken> findByToken(String token) {
        return restorePasswordTokenRepository.findByToken(token);
    }

    // an schedule job call this method every day
    public void deleteUnusableRestoreTokens() {
        List<RestorePasswordToken> restorePasswordTokenList = restorePasswordTokenRepository.findAll()
                .stream()
                .filter(v -> v.getExpiredAt().isBefore(LocalDateTime.now()) || (v.getConfirmedAt() != null && v.getConfirmedAt().plusDays(1).isBefore(LocalDateTime.now())))
                .collect(Collectors.toList());
        restorePasswordTokenRepository.deleteAll(restorePasswordTokenList);
    }

}
