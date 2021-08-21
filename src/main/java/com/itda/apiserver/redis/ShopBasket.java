package com.itda.apiserver.redis;

import com.itda.apiserver.domain.Product;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RedisHash("bascket")
@AllArgsConstructor
@NoArgsConstructor
public class ShopBasket implements Serializable {

    @Id
    private Long userId;
    private List<BascketProduct> products = new ArrayList<>();

    public ShopBasket(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void addProduct(BascketProduct product) {
        products.add(product);
    }

    public BascketProduct getProduct(int idx) {
        return products.get(idx);
    }

}
