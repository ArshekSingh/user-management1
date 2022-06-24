package com.sts.finncub.usermanagement.request;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomDeviceInfoValidator.class)
@Documented
public @interface DeviceInfoValidator {

    String message() default "Please Add Device Info";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
