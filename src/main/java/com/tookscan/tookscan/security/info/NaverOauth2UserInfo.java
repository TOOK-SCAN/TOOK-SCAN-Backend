package com.tookscan.tookscan.security.info;

import com.tookscan.tookscan.security.domain.type.EGender;
import com.tookscan.tookscan.security.info.factory.Oauth2UserInfo;

import java.time.LocalDate;
import java.util.Map;

public class NaverOauth2UserInfo extends Oauth2UserInfo {

    private final Map<String, Object> resp;

    public NaverOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        Object r = attributes.get("response");
        this.resp = (r instanceof Map) ? (Map<String, Object>) r : attributes;
    }

    @Override
    public String getId() {
        Object id = resp.get("id");
        return id != null ? id.toString() : null;
    }

    @Override
    public EGender getGender() {
        Object g = resp.get("gender");
        if (g == null) return EGender.UNKNOWN;
        return switch (g.toString().trim().toUpperCase()) {
            case "M" -> EGender.MALE;
            case "F" -> EGender.FEMALE;
            default  -> EGender.UNKNOWN;
        };
    }

    @Override
    public LocalDate getBirth() {
        Object year = resp.get("birthyear");
        Object mmdd = resp.get("birthday");
        if (year == null || mmdd == null) return null;

        try {
            return LocalDate.parse(year + "-" + mmdd);
        } catch (Exception ignored) {
            return null;
        }
    }
}
