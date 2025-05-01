package com.tookscan.tookscan.core.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ByteSizeValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ByteSize {
    String message() default "{ByteSize}";

    int min();

    int max();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}