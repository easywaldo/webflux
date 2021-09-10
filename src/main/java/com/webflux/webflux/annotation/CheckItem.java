package com.webflux.webflux.annotation;

import com.webflux.webflux.validator.ItemValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy= ItemValidator.class)
@Documented
public @interface CheckItem {
    String message() default "아이템에는 이름이 반드시 있어야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
