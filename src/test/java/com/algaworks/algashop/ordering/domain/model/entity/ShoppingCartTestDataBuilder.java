package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

public class ShoppingCartTestDataBuilder {

    private CustomerId customerId = new CustomerId();

    private boolean withItems = true;

    private ShoppingCartTestDataBuilder() {
    }

    public static ShoppingCartTestDataBuilder aShoppingCart() {
        return new ShoppingCartTestDataBuilder();
    }

    public ShoppingCart build() {
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);

        if (withItems) {
            shoppingCart.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
            shoppingCart.addItem(ProductTestDataBuilder.aProduct_RamMemory().build(), new Quantity(1));
        }

        return shoppingCart;
    }

    public ShoppingCartTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public ShoppingCartTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }
}
