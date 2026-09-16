package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartDoesNotContainItemException;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartDoesNotContainProductException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ShoppingCart {

    private ShoppingCartId id;
    private CustomerId customerId;

    private Money totalAmount;
    private Quantity totalItems;

    private OffsetDateTime createdAt;

    private Set<ShoppingCartItem> items;

    @Builder(builderClassName = "ExistingShoppingCartBuilder", builderMethodName = "existing")
    public ShoppingCart(ShoppingCartId id, CustomerId customerId, Money totalAmount, Quantity totalItems,
                        OffsetDateTime createdAt, Set<ShoppingCartItem> items) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setCreatedAt(createdAt);
        this.setItems(items);
    }

    public static ShoppingCart startShopping(CustomerId customerId) {
        return new ShoppingCart(
                new ShoppingCartId(),
                customerId,
                Money.ZERO,
                Quantity.ZERO,
                OffsetDateTime.now(ZoneId.systemDefault()),
                new HashSet<>()
        );
    }

    public void empty() {
        this.items.removeIf(item -> true);
        this.recalculateTotals();
    }

    public void addItem(Product product, Quantity quantity) {
        Objects.requireNonNull(product);
        Objects.requireNonNull(quantity);

        product.checkOutOfStock();

        this.items.stream()
                .filter(i -> i.productId().equals(product.id()))
                .findFirst()
                .ifPresentOrElse(
                        item -> {
                            item.changeQuantity(item.quantity().add(quantity));
                            item.refresh(product);
                        },
                        () -> this.items.add(ShoppingCartItem.brandNew()
                                .shoppingCartId(this.id())
                                .product(product)
                                .quantity(quantity)
                                .build())
                );

        this.recalculateTotals();
    }

    public void removeItem(ShoppingCartItemId shoppingCartItemId) {
        Objects.requireNonNull(shoppingCartItemId);

        ShoppingCartItem item = this.findItem(shoppingCartItemId);
        this.items.remove(item);

        this.recalculateTotals();
    }

    public void refreshItem(Product product) {
        Objects.requireNonNull(product);

        ShoppingCartItem item = this.findItem(product.id());
        item.refresh(product);

        this.recalculateTotals();
    }

    public void changeItemQuantity(ShoppingCartItemId shoppingCartItemId, Quantity quantity) {
        Objects.requireNonNull(shoppingCartItemId);
        Objects.requireNonNull(quantity);

        ShoppingCartItem item = this.findItem(shoppingCartItemId);
        item.changeQuantity(quantity);

        this.recalculateTotals();
    }

    public ShoppingCartItem findItem(ShoppingCartItemId shoppingCartItemId) {
        Objects.requireNonNull(shoppingCartItemId);
        return this.items.stream()
                .filter(i -> i.id().equals(shoppingCartItemId))
                .findFirst()
                .orElseThrow(() -> new ShoppingCartDoesNotContainItemException(this.id(), shoppingCartItemId));
    }

    public ShoppingCartItem findItem(ProductId productId) {
        Objects.requireNonNull(productId);
        return this.items.stream()
                .filter(i -> i.productId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ShoppingCartDoesNotContainProductException(this.id(), productId));
    }

    public boolean containsUnavailableItems() {
        return this.items.stream().anyMatch(i -> !i.isAvailable());
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public ShoppingCartId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public Set<ShoppingCartItem> items() {
        return Collections.unmodifiableSet(this.items);
    }

    private void recalculateTotals() {
        Money totalAmountRecalculated = this.items.stream()
                .map(ShoppingCartItem::totalAmount)
                .reduce(Money.ZERO, Money::add);
        Quantity totalItemsRecalculated = this.items.stream()
                .map(ShoppingCartItem::quantity)
                .reduce(Quantity.ZERO, Quantity::add);

        this.setTotalAmount(totalAmountRecalculated);
        this.setTotalItems(totalItemsRecalculated);
    }

    private void setId(ShoppingCartId id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    private void setCustomerId(CustomerId customerId) {
        Objects.requireNonNull(customerId);
        this.customerId = customerId;
    }

    private void setTotalAmount(Money totalAmount) {
        Objects.requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setTotalItems(Quantity totalItems) {
        Objects.requireNonNull(totalItems);
        this.totalItems = totalItems;
    }

    private void setCreatedAt(OffsetDateTime createdAt) {
        Objects.requireNonNull(createdAt);
        this.createdAt = createdAt;
    }

    private void setItems(Set<ShoppingCartItem> items) {
        Objects.requireNonNull(items);
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCart that = (ShoppingCart) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
