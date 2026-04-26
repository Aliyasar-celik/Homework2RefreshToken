package com.n11bootcamp.jwtornek.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class TokenManager {

    private static final long ACCESS_TOKEN_VALIDITY_MS = 5 * 60 * 1000;
    private static final long REFRESH_TOKEN_VALIDITY_MS = 24 * 60 * 60 * 1000;

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    public String generateAccessToken(String username) {
        return generateToken(username, ACCESS_TOKEN_VALIDITY_MS, "access");
    }

    public String generateRefreshToken(String username) {
        String tokenId = UUID.randomUUID().toString();
        String refreshToken = generateToken(username, REFRESH_TOKEN_VALIDITY_MS, "refresh");
        refreshTokenStore.put(tokenId, refreshToken);
        return tokenId + ":" + refreshToken;
    }

    public String refreshAccessToken(String tokenIdAndToken) {
        String[] tokenParts = tokenIdAndToken.split(":", 2);
        if (tokenParts.length != 2) {
            return null;
        }

        String tokenId = tokenParts[0];
        String refreshToken = tokenParts[1];
        String savedRefreshToken = refreshTokenStore.get(tokenId);

        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken) || !tokenValidate(refreshToken, "refresh")) {
            return null;
        }

        return generateAccessToken(getUsernameToken(refreshToken));
    }

    public boolean tokenValidate(String token) {
        return tokenValidate(token, "access");
    }

    public boolean tokenValidate(String token, String tokenType) {
        try {
            String username = getUsernameToken(token);
            if (username == null || isExpired(token)) {
                return false;
            }

            Claims claims = getClaims(token);
            return tokenType.equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public boolean isExpired(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }

    private String generateToken(String username, long validity, String tokenType) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("www.opendart.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .claim("type", tokenType)
                .signWith(key)
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }
}
