package com.tookscan.tookscan.security.info;

import com.tookscan.tookscan.security.domain.type.EGender;
import com.tookscan.tookscan.security.info.factory.Oauth2UserInfo;

import java.time.LocalDate;
import java.util.Map;

public class NaverOauth2UserInfo extends Oauth2UserInfo {
    public NaverOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public EGender getGender() {
        String gender = (String) attributes.get("gender");
        if (gender != null) {
            return switch (gender) {
                case "M" -> EGender.MALE;
                case "F" -> EGender.FEMALE;
                default -> EGender.UNKNOWN;
            };
        }
        return EGender.UNKNOWN;
    }

    @Override
    public LocalDate getBirth() {
        String birth = (String) attributes.get("birth");
        String birthYear = (String) attributes.get("birthyear");

        if (birth != null && birthYear != null) {
            try {
                return LocalDate.parse(birthYear + "-" + birth);
            } catch (Exception ignored) {}
        }
        return null;
    }
}
