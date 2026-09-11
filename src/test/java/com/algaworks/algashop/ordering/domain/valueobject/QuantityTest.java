package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class QuantityTest {

    @Test
    void shouldGenerate() {
        assertThat(new Quantity(5).value()).isEqualTo(5);
        assertThat(Quantity.ZERO.value()).isZero();
    }

    @Test
    void shouldNotAllowNull() {
        assertThatNullPointerException().isThrownBy(() -> new Quantity(null));
    }

    @Test
    void shouldNotAllowNegative() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Quantity(-1));
    }

    @Test
    void shouldAdd() {
        assertThat(new Quantity(2).add(new Quantity(3))).isEqualTo(new Quantity(5));
    }

    @Test
    void shouldCompare() {
        assertThat(new Quantity(3)).isGreaterThan(new Quantity(2));
    }

    @Test
    void shouldConvertToString() {
        assertThat(new Quantity(7)).hasToString("7");
    }
}
