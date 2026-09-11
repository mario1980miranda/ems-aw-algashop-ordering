package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ProductNameTest {

    @Test
    void shouldGenerate() {
        assertThat(new ProductName("Notebook")).hasToString("Notebook");
    }

    @Test
    void shouldNotAllowNull() {
        assertThatNullPointerException().isThrownBy(() -> new ProductName(null));
    }

    @Test
    void shouldNotAllowBlank() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new ProductName(" "));
    }
}
