package com.webflux.webflux.cart;

import com.webflux.webflux.annotation.CheckItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Getter
@NoArgsConstructor
@CheckItem(message = "아이템에는 반드시 이름이 지정이 되어야 합니다.")
public class Item {
    @Id
    private String id;
    private String itemName;
    private double itemPrice;

    @Builder
    public Item(
            String id,
            String itemName,
            double itemPrice) {
        this.id = id;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

}
