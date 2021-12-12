package ir.balanxe.modules.registration.entities.refreshtoken.service;

import ir.balanxe.modules.registration.entities.refreshtoken.model.BlackListRefreshToken;
import ir.balanxe.modules.registration.entities.refreshtoken.repository.BlackListRefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlackListRefreshTokenService {

    private final BlackListRefreshTokenRepository blackListRefreshTokenRepository;

    @Autowired
    public BlackListRefreshTokenService(BlackListRefreshTokenRepository blackListRefreshTokenRepository) {
        this.blackListRefreshTokenRepository = blackListRefreshTokenRepository;
    }

    public void saveBlackListRefreshToken(String tokenId, Date expiresAt) {
        blackListRefreshTokenRepository.save(new BlackListRefreshToken(tokenId, expiresAt));
    }

    public boolean searchInBlackList(String tokenId) {
        return blackListRefreshTokenRepository.existsBlackListRefreshTokenByTokenId(tokenId);
    }

    public void deleteExpiredRefreshTokens() {
        List<BlackListRefreshToken> blackListRefreshTokenList =
                blackListRefreshTokenRepository.findAll()
                        .stream()
                        .filter(blackListRefreshToken -> blackListRefreshToken.getExpiresAt().before(new Date()))
                        .collect(Collectors.toList());

        blackListRefreshTokenRepository.deleteAll(blackListRefreshTokenList);
    }
}
