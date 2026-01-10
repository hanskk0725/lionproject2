package com.example.lionproject2backend.global.security.jwt;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;
    private final Long userId;

    // 인증 전
    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.userId = null;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(Long userId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = null;
        this.userId = userId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
