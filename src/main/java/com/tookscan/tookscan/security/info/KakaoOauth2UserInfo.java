package com.tookscan.tookscan.security.info;

import com.tookscan.tookscan.security.domain.type.EGender;
import com.tookscan.tookscan.security.info.factory.Oauth2UserInfo;

import java.util.Map;

public class KakaoOauth2UserInfo extends Oauth2UserInfo {
    public KakaoOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    public EGender getGender() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount != null && kakaoAccount.get("gender") != null) {
            return switch (kakaoAccount.get("gender").toString()) {
                case "male" -> EGender.MALE;
                case "female" -> EGender.FEMALE;
                default -> EGender.UNKNOWN;
            };
        }
        return EGender.UNKNOWN;
    }

    @Override
    public Integer getBirthYear() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;
        String birthyear = (String) kakaoAccount.get("birthyear");

        if (birthyear == null) return null;

        try {
            return Integer.parseInt(birthyear);
        } catch (Exception ignored) {
            return null;
        }
    }
}
