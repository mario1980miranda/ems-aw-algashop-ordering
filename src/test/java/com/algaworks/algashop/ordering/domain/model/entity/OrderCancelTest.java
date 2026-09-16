package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.OrderStatusCannotBeChangedException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OrderCancelTest {

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"DRAFT", "PLACED", "PAID", "READY"})
    void shouldCancelOrder(OrderStatus status) {
        Order order = OrderTestDataBuilder.anOrder().status(status).build();
        OffsetDateTime before = OffsetDateTime.now(ZoneId.systemDefault());

        order.cancel();

        assertThat(order.status()).isEqualTo(OrderStatus.CANCELED);
        assertThat(order.canceledAt()).isNotNull().isAfterOrEqualTo(before);
    }

    @Test
    void shouldNotAllowCancelAlreadyCanceledOrder() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build();
        OffsetDateTime canceledAtBefore = order.canceledAt();

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::cancel);

        assertThat(order.status()).isEqualTo(OrderStatus.CANCELED);
        assertThat(order.canceledAt()).isEqualTo(canceledAtBefore);
    }
}
