package com.tookscan.tookscan.security.info.factory;

import com.tookscan.tookscan.security.domain.type.EGender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public abstract class Oauth2UserInfo {
    protected final Map<String, Object> attributes;
    public abstract String getId();
    public abstract EGender getGender();
    public abstract Integer getBirthYear();
}
