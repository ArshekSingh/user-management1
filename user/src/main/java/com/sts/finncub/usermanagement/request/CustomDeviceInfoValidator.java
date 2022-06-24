package com.sts.finncub.usermanagement.request;


import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomDeviceInfoValidator implements ConstraintValidator<DeviceInfoValidator, Object> {
    @Override
    public void initialize(DeviceInfoValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object deviceInfo, ConstraintValidatorContext context) {
        boolean isValidDeviceInfoObject = false;
        if (deviceInfo != null && StringUtils.hasText(deviceInfo.toString())) {
            isValidDeviceInfoObject = true;
        }
        return isValidDeviceInfoObject;
    }
}
