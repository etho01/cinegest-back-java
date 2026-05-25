package com.cinegest.back.global;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String type;
    private final Integer originId;

    public CustomAuthenticationToken(String email, String password, String type, Integer originId) {
        super(email, password);
        this.type = type;
        this.originId = originId;
    }

    public CustomAuthenticationToken(
            Object principal,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities,
            String type,
            Integer originId
    ) {
        super(principal, credentials, authorities);
        this.type = type;
        this.originId = originId;
    }

    public String getType() {
        return type;
    }

    public Integer getOriginId() {
        return originId;
    }
}
