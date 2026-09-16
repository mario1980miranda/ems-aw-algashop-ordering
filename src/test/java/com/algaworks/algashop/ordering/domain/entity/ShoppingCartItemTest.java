package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.ShoppingCartItemIncompatibleProductException;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ShoppingCartItemTest {

    private ShoppingCartItem aNotebookItem() {
        return ShoppingCartItem.brandNew()
                .shoppingCartId(new ShoppingCartId())
                .product(ProductTestDataBuilder.aProduct().build())
                .quantity(new Quantity(2))
                .build();
    }

    @Test
    void shouldCreateBrandNewItem() {
        ShoppingCartId shoppingCartId = new ShoppingCartId();
        Product product = ProductTestDataBuilder.aProduct().build();

        ShoppingCartItem item = ShoppingCartItem.brandNew()
                .shoppingCartId(shoppingCartId)
                .product(product)
                .quantity(new Quantity(2))
                .build();

        assertWith(item,
                i -> assertThat(i.id()).isNotNull(),
                i -> assertThat(i.shoppingCartId()).isEqualTo(shoppingCartId),
                i -> assertThat(i.productId()).isEqualTo(product.id()),
                i -> assertThat(i.productName()).isEqualTo(product.name()),
                i -> assertThat(i.price()).isEqualTo(product.price()),
                i -> assertThat(i.isAvailable()).isTrue(),
                i -> assertThat(i.totalAmount()).isEqualTo(new Money("6000"))
        );
    }

    @Test
    void shouldChangeQuantityAndRecalculateTotal() {
        ShoppingCartItem item = aNotebookItem();

        item.changeQuantity(new Quantity(3));

        assertThat(item.quantity()).isEqualTo(new Quantity(3));
        assertThat(item.totalAmount()).isEqualTo(new Money("9000"));
    }

    @Test
    void shouldNotAllowChangeQuantityToZero() {
        ShoppingCartItem item = aNotebookItem();

        assertThatIllegalArgumentException().isThrownBy(() -> item.changeQuantity(Quantity.ZERO));
        assertThat(item.quantity()).isEqualTo(new Quantity(2));
    }

    @Test
    void shouldRefreshItem() {
        ShoppingCartItem item = aNotebookItem();
        Product updated = ProductTestDataBuilder.aProduct().price(new Money("2000")).inStock(false).build();

        item.refresh(updated);

        assertThat(item.price()).isEqualTo(new Money("2000"));
        assertThat(item.isAvailable()).isFalse();
        assertThat(item.totalAmount()).isEqualTo(new Money("4000"));
    }

    @Test
    void shouldNotAllowRefreshWithIncompatibleProduct() {
        ShoppingCartItem item = aNotebookItem();
        Product mousePad = ProductTestDataBuilder.aProduct_MousePad().build();

        assertThatExceptionOfType(ShoppingCartItemIncompatibleProductException.class)
                .isThrownBy(() -> item.refresh(mousePad));
        assertThat(item.price()).isEqualTo(new Money("3000"));
    }

    @Test
    void shouldBeEqualWhenSameId() {
        ShoppingCartItem item = aNotebookItem();
        ShoppingCartItem sameItem = ShoppingCartItem.existing()
                .id(item.id())
                .shoppingCartId(new ShoppingCartId())
                .productId(item.productId())
                .productName(item.productName())
                .price(item.price())
                .quantity(new Quantity(1))
                .available(true)
                .totalAmount(item.price())
                .build();

        assertThat(sameItem).isEqualTo(item).hasSameHashCodeAs(item);
        assertThat(aNotebookItem()).isNotEqualTo(item);
    }
}
