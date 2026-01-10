package com.example.lionproject2backend.global.security.jwt;

import com.example.lionproject2backend.user.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final JwtProperties props;

    public JwtUtil(JwtProperties props) {
        this.props = props;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(props.getSecret())
        );
    }

    public String createAccessToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("role", user.getUserRole().name())
                .claim("type", TokenType.ACCESS.name())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + props.getAccessExpMs()))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("type", TokenType.REFRESH.name())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + props.getRefreshExpMs()))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void validate(String token) {
        parseClaims(token);
    }

    public void validateType(String token, TokenType expected) {
        Claims claims = parseClaims(token);
        String type = claims.get("type", String.class);
        if (type == null || !type.equals(expected.name())) {
            throw new JwtException("Invalid token type");
        }
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
}
