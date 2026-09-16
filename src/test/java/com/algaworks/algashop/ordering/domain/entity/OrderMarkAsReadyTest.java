package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OrderMarkAsReadyTest {

    @Test
    void shouldMarkPaidOrderAsReady() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        OffsetDateTime before = OffsetDateTime.now(ZoneId.systemDefault());

        order.markAsReady();

        assertThat(order.isReady()).isTrue();
        assertThat(order.readyAt()).isNotNull().isAfterOrEqualTo(before);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"DRAFT", "PLACED", "READY"})
    void shouldNotAllowMarkAsReadyWhenNotPaid(OrderStatus status) {
        Order order = OrderTestDataBuilder.anOrder().status(status).build();
        OffsetDateTime readyAtBefore = order.readyAt();

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::markAsReady);

        assertThat(order.status()).isEqualTo(status);
        assertThat(order.readyAt()).isEqualTo(readyAtBefore);
    }
}
