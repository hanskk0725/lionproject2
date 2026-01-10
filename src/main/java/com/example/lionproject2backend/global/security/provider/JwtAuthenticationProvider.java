package com.example.lionproject2backend.global.security.provider;

import com.example.lionproject2backend.global.exception.custom.CustomException;
import com.example.lionproject2backend.global.exception.custom.ErrorCode;
import com.example.lionproject2backend.global.security.jwt.JwtAuthenticationToken;
import com.example.lionproject2backend.global.security.jwt.JwtUtil;
import com.example.lionproject2backend.global.security.jwt.TokenType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();

        try {
            jwtUtil.validate(token);
            jwtUtil.validateType(token, TokenType.ACCESS);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        Long userId = jwtUtil.getUserId(token);
        String role = "ROLE_" + jwtUtil.getRole(token); // 상의해보고 결정하기!!!

        return new JwtAuthenticationToken(
                userId,
                List.of(new SimpleGrantedAuthority(role))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}