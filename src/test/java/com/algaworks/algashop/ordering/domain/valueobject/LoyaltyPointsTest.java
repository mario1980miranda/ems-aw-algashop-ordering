package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class LoyaltyPointsTest {

    @Test
    void shouldGenerate() {
        LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);
        assertThat(loyaltyPoints.value()).isEqualTo(10);
    }

    @Test
    void shouldAddValue() {
        LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);
        var loyaltyPointsUpdated = loyaltyPoints.add(5);
        assertThat(loyaltyPointsUpdated.value()).isEqualTo(15);
    }

    @Test
    void shouldNotAddValue() {
        LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> loyaltyPoints.add(-5));
        assertThat(loyaltyPoints.value()).isEqualTo(10);
    }

    @Test
    void shouldNotAddZeroValue() {
        LoyaltyPoints loyaltyPoints = new LoyaltyPoints(10);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> loyaltyPoints.add(-0));
        assertThat(loyaltyPoints.value()).isEqualTo(10);
    }
}