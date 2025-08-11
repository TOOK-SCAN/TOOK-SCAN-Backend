package com.tookscan.tookscan.security.info;

import com.tookscan.tookscan.security.domain.type.EGender;
import com.tookscan.tookscan.security.info.factory.Oauth2UserInfo;

import java.time.LocalDate;
import java.util.Map;

public class GoogleOauth2UserInfo extends Oauth2UserInfo {
    public GoogleOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public EGender getGender() {
        if (attributes.get("gender") != null) {
            return switch ((String) attributes.get("gender")) {
                case "male" -> EGender.MALE;
                case "female" -> EGender.FEMALE;
                default -> EGender.UNKNOWN;
            };
        }
        return EGender.UNKNOWN;
    }

    @Override
    public Integer getBirthYear() {
        if (attributes.get("birthdate") == null) return null;
        String birthdate = (String) attributes.get("birthdate");

        if (birthdate == null || birthdate.isEmpty()) return null;

        try {
            LocalDate date = LocalDate.parse(birthdate);
            return date.getYear();
        } catch (Exception e) {
            return null;
        }
    }
}
