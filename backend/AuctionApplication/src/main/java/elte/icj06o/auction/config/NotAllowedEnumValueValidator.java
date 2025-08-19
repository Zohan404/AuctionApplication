package elte.icj06o.auction.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotAllowedEnumValueValidator implements ConstraintValidator<NotAllowedEnumValue, Enum<?>> {
    private String prohibited;

    @Override
    public void initialize(NotAllowedEnumValue constraintAnnotation) {
        this.prohibited = constraintAnnotation.prohibited();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return !value.name().equals(prohibited);
    }
}
