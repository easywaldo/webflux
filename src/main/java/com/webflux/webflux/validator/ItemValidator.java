package com.webflux.webflux.validator;

import com.webflux.webflux.annotation.CheckItem;
import com.webflux.webflux.cart.Item;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemValidator implements ConstraintValidator<CheckItem, Item> {

    @Override
    public void initialize(CheckItem constraintAnnotation) {
    }

    @Override
    public boolean isValid(Item value, ConstraintValidatorContext context) {
        return value.getItemName().length() != 0;
    }
}
