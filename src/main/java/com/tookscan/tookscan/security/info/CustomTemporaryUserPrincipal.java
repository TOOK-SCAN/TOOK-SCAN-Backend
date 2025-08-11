package com.tookscan.tookscan.security.info;

import com.tookscan.tookscan.security.domain.type.EGender;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class CustomTemporaryUserPrincipal implements OAuth2User {

    private final String serialId;
    private final ESecurityProvider provider;
    private final EGender gender;
    private final Integer birth;

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return serialId;
    }
}
