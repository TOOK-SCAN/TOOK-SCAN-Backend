package com.tookscan.tookscan.core.validator;

import com.tookscan.tookscan.core.annotation.validation.ValidPassword;
import com.tookscan.tookscan.security.presentation.dto.request.SignUpDefaultRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, SignUpDefaultRequestDto> {

    @Override
    public boolean isValid(SignUpDefaultRequestDto dto, ConstraintValidatorContext context) {
        String password = dto.password();
        String id = dto.serialId();

        if (password == null || id == null) {
            return true;
        }

        // 아이디와 비밀번호가 같은지 검증
        if (password.equals(id)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("아이디와 비밀번호는 같을 수 없습니다.")
                    .addPropertyNode("password").addConstraintViolation();
            return false;
        }

        // 동일 문자 5자 이상 반복 검증
        if (hasRepeatedChars(password)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("동일 문자를 5자 이상 사용할 수 없습니다.")
                    .addPropertyNode("password").addConstraintViolation();
            return false;
        }

        // 연속 문자 (오름차순 또는 내림차순) 5자 이상 검증
        if (hasSequentialChars(password)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("연속된 문자를 5자 이상 사용할 수 없습니다.")
                    .addPropertyNode("password").addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean hasRepeatedChars(String str) {
        for (int i = 0; i <= str.length() - 5; i++) {
            char c = str.charAt(i);
            boolean allSame = true;
            for (int j = 1; j < 5; j++) {
                if (str.charAt(i + j) != c) {
                    allSame = false;
                    break;
                }
            }
            if (allSame) return true;
        }
        return false;
    }

    private boolean hasSequentialChars(String str) {
        for (int i = 0; i <= str.length() - 5; i++) {
            boolean increasing = true;
            boolean decreasing = true;
            for (int j = 1; j < 5; j++) {
                if (str.charAt(i + j) - str.charAt(i + j - 1) != 1) {
                    increasing = false;
                }
                if (str.charAt(i + j) - str.charAt(i + j - 1) != -1) {
                    decreasing = false;
                }
            }
            if (increasing || decreasing) return true;
        }
        return false;
    }
}

