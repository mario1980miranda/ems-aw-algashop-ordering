package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderItemId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OrderChangingTest {

    @Test
    void shouldAllowChangesWhenDraft() {
        Order order = OrderTestDataBuilder.anOrder().withItems(false).build();
        Shipping shipping = OrderTestDataBuilder.aShippingAlt();
        Billing billing = OrderTestDataBuilder.aBilling();

        order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));
        OrderItemId itemId = order.items().iterator().next().id();
        order.changeItemQuantity(itemId, new Quantity(3));
        order.changeShipping(shipping);
        order.changeBilling(billing);
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);

        assertThat(order.items()).hasSize(1);
        assertThat(order.totalItems()).isEqualTo(new Quantity(3));
        assertThat(order.shipping()).isEqualTo(shipping);
        assertThat(order.billing()).isEqualTo(billing);
        assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PLACED", "PAID"})
    void shouldNotAllowAddItemWhenNotDraft(OrderStatus status) {
        Order order = OrderTestDataBuilder.anOrder().status(status).build();
        assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1)));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PLACED", "PAID"})
    void shouldNotAllowChangeItemQuantityWhenNotDraft(OrderStatus status) {
        Order order = OrderTestDataBuilder.anOrder().status(status).build();
        OrderItemId itemId = order.items().iterator().next().id();
        assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeItemQuantity(itemId, new Quantity(5)));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PLACED", "PAID"})
    void shouldNotAllowChangeShippingWhenNotDraft(OrderStatus status) {
        Order order = OrderTestDataBuilder.anOrder().status(status).build();
        assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeShipping(OrderTestDataBuilder.aShippingAlt()));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PLACED", "PAID"})
    void shouldNotAllowChangeBillingWhenNotDraft(OrderStatus status) {
        Order order = OrderTestDataBuilder.anOrder().status(status).build();
        assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeBilling(OrderTestDataBuilder.aBilling()));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PLACED", "PAID"})
    void shouldNotAllowChangePaymentMethodWhenNotDraft(OrderStatus status) {
        Order order = OrderTestDataBuilder.anOrder().status(status).build();
        assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changePaymentMethod(PaymentMethod.CREDIT_CARD))
                .withMessageContaining(status.name());
    }

    @Test
    void shouldNotAllowChangesAfterPlacingOrder() {
        Order order = OrderTestDataBuilder.anOrder().build();
        order.place();
        assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeBilling(OrderTestDataBuilder.aBilling()));
    }
}
