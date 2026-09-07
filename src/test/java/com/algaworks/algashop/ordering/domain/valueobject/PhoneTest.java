package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PhoneTest {

    @Test
    void given_validValue_whenCreate_shouldKeepValue() {
        Phone phone = new Phone("478-256-2504");

        Assertions.assertThat(phone.value()).isEqualTo("478-256-2504");
    }

    @Test
    void given_nullValue_whenCreate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Phone(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void given_blankValue_whenCreate_shouldGenerateException(String blank) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Phone(blank));
    }

    @Test
    void given_phone_whenCallToString_shouldReturnWrappedValue() {
        Assertions.assertThat(new Phone("478-256-2504")).hasToString("478-256-2504");
    }

    @Test
    void given_sameValue_whenCompare_shouldBeEqual() {
        Assertions.assertThat(new Phone("478-256-2504"))
                .isEqualTo(new Phone("478-256-2504"))
                .hasSameHashCodeAs(new Phone("478-256-2504"))
                .isNotEqualTo(new Phone("000-000-0000"));
    }
}
