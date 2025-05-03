package com.tookscan.tookscan.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.nio.charset.StandardCharsets;

public class ByteSizeValidator implements ConstraintValidator<ByteSize, String> {
    private int min, max;

    public void initialize(ByteSize anno) {
        this.min = anno.min();
        this.max = anno.max();
    }

    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) {
            return true; // @NotBlank 와 조합
        }
        int bytes = value.getBytes(StandardCharsets.UTF_8).length;
        return bytes >= min && bytes <= max;
    }
}