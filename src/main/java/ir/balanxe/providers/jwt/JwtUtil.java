package ir.balanxe.providers.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtUtil {

    private final String INVALID_REFRESH_TOKEN = " .وارد شده معتبر نمیباشد refresh token";
    private final KeyPair KEY_PAIR = Keys.keyPairFor(SignatureAlgorithm.RS256);
    private final String GRANT_TYPE_TITLE = "grant_type";
    private final String ACCESS_TOKEN_GRANT_TYPE = "access_token";
    private final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

    // TODO: remove this line for production
    private final String SECRET_KEY_DEBUG = "Afsdg656FGDS46DFcvAfsdg656FGDS46DFcvAfsdg656FGDS46DFcv";

    private Date currentDate;
    private Long accessTokenExpiration;
    private Long refreshTokenExpiration;

    public String generateToken(String subject, boolean refreshToken) {
        currentDate = new Date();
        accessTokenExpiration = currentDate.getTime() + (1000 * 60 * 60 * 24 * 15);
        refreshTokenExpiration = currentDate.getTime() + (1000 * 60 * 60 * 6);

        if (refreshToken)
            return createRefreshToken(subject);
        return createAccessToken(subject);
    }

    private String createAccessToken(String subject) {
        Map<String, String> map = new HashMap<>();
        map.put(GRANT_TYPE_TITLE, ACCESS_TOKEN_GRANT_TYPE);
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(map)
                .setSubject(subject)
                .setIssuedAt(currentDate)
                .setExpiration(new Date(accessTokenExpiration))
//                .signWith(KEY_PAIR.getPrivate(), SignatureAlgorithm.RS256)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY_DEBUG)
                .compact();

    }

    private String createRefreshToken(String subject) {
        Map<String, String> map = new HashMap<>();
        map.put(GRANT_TYPE_TITLE, REFRESH_TOKEN_GRANT_TYPE);
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(map)
                .setSubject(subject)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(currentDate)
                .setExpiration(new Date(refreshTokenExpiration))
//                .signWith(KEY_PAIR.getPrivate(), SignatureAlgorithm.RS256)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY_DEBUG)
                .compact();

    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaim(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaim(String token) {
//        return Jwts.parserBuilder().setSigningKey(KEY_PAIR.getPublic()).build().parseClaimsJws(token).getBody();
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY_DEBUG).build().parseClaimsJws(token).getBody();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractGrantType(String token) {
        try {
            return extractAllClaim(token).get(GRANT_TYPE_TITLE, String.class);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_REFRESH_TOKEN);
        }
    }

    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject));
    }

}
