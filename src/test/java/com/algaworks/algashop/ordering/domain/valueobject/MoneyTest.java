package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class MoneyTest {

    @Test
    void shouldGenerateWithScaleTwo() {
        assertThat(new Money("10.555").value()).isEqualTo(new BigDecimal("10.56"));
        assertThat(new Money(new BigDecimal("10")).value()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    void shouldNotAllowNull() {
        assertThatNullPointerException().isThrownBy(() -> new Money((BigDecimal) null));
    }

    @Test
    void shouldNotAllowNegative() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Money("-0.01"));
    }

    @Test
    void shouldMultiplyByQuantity() {
        assertThat(new Money("10.00").multiply(new Quantity(3)))
                .isEqualTo(new Money("30.00"));
    }

    @Test
    void shouldNotMultiplyByQuantityLowerThanOne() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Money("10.00").multiply(Quantity.ZERO));
    }

    @Test
    void shouldAdd() {
        assertThat(new Money("10.00").add(new Money("5.50")))
                .isEqualTo(new Money("15.50"));
    }

    @Test
    void shouldDivide() {
        assertThat(new Money("10.00").divide(new Money("4.00")))
                .isEqualTo(new Money("2.50"));
    }

    @Test
    void shouldCompare() {
        assertThat(new Money("10.00")).isGreaterThan(new Money("9.99"));
        assertThat(new Money("10.00")).isEqualByComparingTo(new Money("10.000"));
    }

    @Test
    void shouldConvertToString() {
        assertThat(new Money("10.5")).hasToString("10.50");
    }

    @Test
    void shouldExposeZero() {
        assertThat(Money.ZERO.value()).isEqualTo(new BigDecimal("0.00"));
    }
}
