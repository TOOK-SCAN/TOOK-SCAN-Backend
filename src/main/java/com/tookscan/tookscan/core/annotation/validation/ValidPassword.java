package com.tookscan.tookscan.core.annotation.validation;

import com.tookscan.tookscan.core.validator.PasswordConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordConstraintValidator.class)
public @interface ValidPassword {
    String message() default "사용할 수 없는 비밀번호입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

