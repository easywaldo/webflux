package com.webflux.webflux.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Getter
@NoArgsConstructor
public class Item {
    @Id
    private String id;
    private String itemName;
    private double itemPrice;

    @Builder
    public Item(String itemName,
                double itemPrice) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

}
