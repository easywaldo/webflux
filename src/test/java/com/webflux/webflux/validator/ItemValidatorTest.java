package com.webflux.webflux.validator;

import com.webflux.webflux.cart.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Validator;

@SpringBootTest
public class ItemValidatorTest {

    @Autowired
    public Validator validator;

    @Test
    public void ItemCheckTest() {
        Item testItem = Item.builder().id("testId").itemPrice(100).itemName("").build();
        var result = validator.validate(testItem);
        System.out.println(result);
    }

}