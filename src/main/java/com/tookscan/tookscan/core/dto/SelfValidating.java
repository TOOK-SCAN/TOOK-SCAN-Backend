package com.tookscan.tookscan.core.dto;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.core.utility.StructuredLoggerUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * SelfValidating 을 상속받아서 사용하는 클래스는
 * 해당 클래스가 만들어질 때 Validation 을 수행한다.
 * @param <T>
 */
@Slf4j
public abstract class SelfValidating<T> {

    private final Validator validator;

    public SelfValidating() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Evaluates all Bean Validations on the attributes of this
     * instance.
     */
    protected void validateSelf() {
        Set<ConstraintViolation<T>> violations = validator.validate((T) this);
        if (!violations.isEmpty()) {
            StructuredLoggerUtil.error(log)
                    .message("Validation failed for object")
                    .details(Map.of(
                            "violation_count", violations.size(),
                            "violations", violations.stream()
                                    .map(violation -> Map.of(
                                            "property", violation.getPropertyPath().toString(),
                                            "message", violation.getMessage(),
                                            "invalid_value", violation.getInvalidValue()
                                    ))
                                    .toList()
                    ))
                    .log();
            throw new CommonException(ErrorCode.INTERNAL_DATA_ERROR);
        }
    }
}
