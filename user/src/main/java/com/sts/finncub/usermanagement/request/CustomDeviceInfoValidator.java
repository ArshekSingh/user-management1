package com.sts.finncub.usermanagement.request;


import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Collections;

public class CustomDeviceInfoValidator implements ConstraintValidator<DeviceInfoValidator, Object> {
    @Override
    public void initialize(DeviceInfoValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean isValidUser = false;
        if (value != null && StringUtils.hasText(value.toString())) {
            isValidUser = true;
        }
        return isValidUser;
    }
}
