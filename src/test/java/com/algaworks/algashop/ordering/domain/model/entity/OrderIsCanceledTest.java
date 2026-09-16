package com.algaworks.algashop.ordering.domain.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderIsCanceledTest {

    @Test
    void shouldReturnTrueWhenCanceled() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build();
        assertThat(order.isCanceled()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"CANCELED"}, mode = EnumSource.Mode.EXCLUDE)
    void shouldReturnFalseWhenNotCanceled(OrderStatus status) {
        Order order = OrderTestDataBuilder.anOrder().status(status).build();
        assertThat(order.isCanceled()).isFalse();
    }
}
