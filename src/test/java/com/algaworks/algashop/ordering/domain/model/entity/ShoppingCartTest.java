package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartDoesNotContainItemException;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartDoesNotContainProductException;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartItemIncompatibleProductException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.*;

class ShoppingCartTest {

    @Test
    void shouldStartEmptyShoppingCart() {
        CustomerId customerId = new CustomerId();

        ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);

        assertWith(shoppingCart,
                c -> assertThat(c.id()).isNotNull(),
                c -> assertThat(c.customerId()).isEqualTo(customerId),
                c -> assertThat(c.totalAmount()).isEqualTo(Money.ZERO),
                c -> assertThat(c.totalItems()).isEqualTo(Quantity.ZERO),
                c -> assertThat(c.createdAt()).isNotNull(),
                c -> assertThat(c.isEmpty()).isTrue(),
                c -> assertThat(c.containsUnavailableItems()).isFalse()
        );
    }

    @Test
    void shouldNotAllowAddOutOfStockProduct() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        Product product = ProductTestDataBuilder.aProductUnavailable().build();

        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> shoppingCart.addItem(product, new Quantity(1)));
        assertThat(shoppingCart.isEmpty()).isTrue();
    }

    @Test
    void shouldSumQuantityAndRefreshItemWhenAddingSameProductTwice() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCart.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));

        Product updatedProduct = ProductTestDataBuilder.aProduct()
                .name(new ProductName("Notebook X12"))
                .price(new Money("2500"))
                .build();
        shoppingCart.addItem(updatedProduct, new Quantity(2));

        assertThat(shoppingCart.items()).hasSize(1);
        ShoppingCartItem item = shoppingCart.findItem(ProductTestDataBuilder.DEFAULT_PRODUCT_ID);
        assertWith(item,
                i -> assertThat(i.quantity()).isEqualTo(new Quantity(3)),
                i -> assertThat(i.price()).isEqualTo(new Money("2500")),
                i -> assertThat(i.productName()).isEqualTo(new ProductName("Notebook X12")),
                i -> assertThat(i.totalAmount()).isEqualTo(new Money("7500"))
        );
        assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(3));
        assertThat(shoppingCart.totalAmount()).isEqualTo(new Money("7500"));
    }

    @Test
    void shouldAddDifferentProductsAsDistinctItems() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();

        shoppingCart.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
        shoppingCart.addItem(ProductTestDataBuilder.aProduct_MousePad().build(), new Quantity(1));
        shoppingCart.addItem(ProductTestDataBuilder.aProduct_MousePad().build(), new Quantity(2));

        assertThat(shoppingCart.items()).hasSize(2);
        assertThat(shoppingCart.findItem(ProductTestDataBuilder.MOUSE_PAD_PRODUCT_ID).quantity())
                .isEqualTo(new Quantity(3));
        assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(5));
        assertThat(shoppingCart.totalAmount()).isEqualTo(new Money("6300"));
    }

    @Test
    void shouldRemoveItemAndRecalculateTotals() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCartItem ramMemory = shoppingCart.findItem(ProductTestDataBuilder.RAM_MEMORY_PRODUCT_ID);

        shoppingCart.removeItem(ramMemory.id());

        assertThat(shoppingCart.items()).hasSize(1).doesNotContain(ramMemory);
        assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(2));
        assertThat(shoppingCart.totalAmount()).isEqualTo(new Money("6000"));
    }

    @Test
    void shouldNotAllowRemoveNonExistingItem() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
                .isThrownBy(() -> shoppingCart.removeItem(new ShoppingCartItemId()));
    }

    @Test
    void shouldEmptyShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        shoppingCart.empty();

        assertThat(shoppingCart.isEmpty()).isTrue();
        assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
        assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
    }

    @Test
    void shouldRefreshItemAndRecalculateTotals() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Product unavailableRam = ProductTestDataBuilder.aProduct_RamMemory()
                .price(new Money("300"))
                .inStock(false)
                .build();

        shoppingCart.refreshItem(unavailableRam);

        assertThat(shoppingCart.containsUnavailableItems()).isTrue();
        assertThat(shoppingCart.totalAmount()).isEqualTo(new Money("6300"));
    }

    @Test
    void shouldNotAllowRefreshItemWithProductNotInCart() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Product mousePad = ProductTestDataBuilder.aProduct_MousePad().build();

        assertThatExceptionOfType(ShoppingCartDoesNotContainProductException.class)
                .isThrownBy(() -> shoppingCart.refreshItem(mousePad));
    }

    @Test
    void shouldNotAllowRefreshItemWithIncompatibleProduct() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCartItem notebook = shoppingCart.findItem(ProductTestDataBuilder.DEFAULT_PRODUCT_ID);
        Product mousePad = ProductTestDataBuilder.aProduct_MousePad().build();

        assertThatExceptionOfType(ShoppingCartItemIncompatibleProductException.class)
                .isThrownBy(() -> notebook.refresh(mousePad));
    }

    @Test
    void shouldChangeItemQuantityAndRecalculateTotals() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCartItem ramMemory = shoppingCart.findItem(ProductTestDataBuilder.RAM_MEMORY_PRODUCT_ID);

        shoppingCart.changeItemQuantity(ramMemory.id(), new Quantity(4));

        assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(6));
        assertThat(shoppingCart.totalAmount()).isEqualTo(new Money("6800"));
    }

    @Test
    void shouldNotAllowModifyItemsCollection() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> shoppingCart.items().clear());
    }

    @Test
    void shouldBeEqualWhenSameId() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCart sameCart = ShoppingCart.existing()
                .id(shoppingCart.id())
                .customerId(new CustomerId())
                .totalAmount(Money.ZERO)
                .totalItems(Quantity.ZERO)
                .createdAt(shoppingCart.createdAt())
                .items(new HashSet<>())
                .build();

        assertThat(sameCart).isEqualTo(shoppingCart).hasSameHashCodeAs(shoppingCart);
        assertThat(ShoppingCartTestDataBuilder.aShoppingCart().build()).isNotEqualTo(shoppingCart);
    }
}
