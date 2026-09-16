package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;

public class ProductTestDataBuilder {

    public static final ProductId DEFAULT_PRODUCT_ID = new ProductId();
    public static final ProductId UNAVAILABLE_PRODUCT_ID = new ProductId();
    public static final ProductId RAM_MEMORY_PRODUCT_ID = new ProductId();
    public static final ProductId MOUSE_PAD_PRODUCT_ID = new ProductId();

    private ProductTestDataBuilder() {
    }

    public static Product.ProductBuilder aProduct() {
        return Product.builder()
                .id(DEFAULT_PRODUCT_ID)
                .name(new ProductName("Notebook X11"))
                .price(new Money("3000"))
                .inStock(true);
    }

    public static Product.ProductBuilder aProductUnavailable() {
        return Product.builder()
                .id(UNAVAILABLE_PRODUCT_ID)
                .name(new ProductName("Desktop FX9000"))
                .price(new Money("5000"))
                .inStock(false);
    }

    public static Product.ProductBuilder aProduct_RamMemory() {
        return Product.builder()
                .id(RAM_MEMORY_PRODUCT_ID)
                .name(new ProductName("4GB RAM Memory"))
                .price(new Money("200"))
                .inStock(true);
    }

    public static Product.ProductBuilder aProduct_MousePad() {
        return Product.builder()
                .id(MOUSE_PAD_PRODUCT_ID)
                .name(new ProductName("Mouse Pad"))
                .price(new Money("100"))
                .inStock(true);
    }
}
