package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.model.exception.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OrderRemoveItemTest {

    @Test
    void shouldRemoveItemAndRecalculateTotals() {
        Order order = OrderTestDataBuilder.anOrder().build();
        OrderItem ramMemory = order.items().stream()
                .filter(i -> i.quantity().equals(new Quantity(1)))
                .findFirst()
                .orElseThrow();

        order.removeItem(ramMemory.id());

        assertThat(order.items()).hasSize(1).doesNotContain(ramMemory);
        assertThat(order.totalItems()).isEqualTo(new Quantity(2));
        assertThat(order.totalAmount()).isEqualTo(new Money("6010"));
    }

    @Test
    void shouldNotAllowRemoveNonExistingItem() {
        Order order = OrderTestDataBuilder.anOrder().build();

        assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(() -> order.removeItem(new OrderItemId()));
        assertThat(order.items()).hasSize(2);
    }

    @Test
    void shouldNotAllowRemoveItemWhenNotDraft() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        OrderItemId itemId = order.items().iterator().next().id();

        assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.removeItem(itemId));
        assertThat(order.items()).hasSize(2);
    }
}
