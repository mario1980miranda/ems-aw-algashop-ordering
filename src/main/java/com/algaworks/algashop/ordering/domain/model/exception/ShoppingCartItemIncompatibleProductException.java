package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT;

public class ShoppingCartItemIncompatibleProductException extends DomainException {
    public ShoppingCartItemIncompatibleProductException(ShoppingCartItemId id, ProductId itemProductId,
                                                        ProductId productId) {
        super(String.format(ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT, id, itemProductId, productId));
    }
}
