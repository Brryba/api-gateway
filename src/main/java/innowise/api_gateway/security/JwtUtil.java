package innowise.api_gateway.security;

import innowise.api_gateway.exception.security.InvalidJwtTokenException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtUtil {
    @Value("${security.jwt-key}")
    private String jwtKey;
    private SecretKey key;

    @PostConstruct
    public void setJwtKey() {
        this.key = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    public void validateJwtToken(String accessToken) throws InvalidJwtTokenException {
        log.info("Validating Bearer token: {}", accessToken);
        JwtParser jwtParser = Jwts.parser().
                verifyWith(key)
                .build();
        try {
            jwtParser.parse(accessToken);
            log.info("WT token {} is valid", accessToken);
        } catch (Exception e) {
            log.warn("JWT token {} is invalid: {}", accessToken, e.getMessage());
            throw new InvalidJwtTokenException(e.getMessage());
        }
    }

    public long getUserIdFromToken(String accessToken) {
        String idSubject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getSubject();
        return Long.parseLong(idSubject);
    }
}
