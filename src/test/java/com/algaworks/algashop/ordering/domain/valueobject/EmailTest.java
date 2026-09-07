package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "jhon.doe@gmail.com",
            "jhon+tag@sub.domain.com",
            "j@d.co"
    })
    void given_validEmail_whenCreate_shouldKeepValue(String value) {
        Assertions.assertThat(new Email(value).value()).isEqualTo(value);
    }

    @Test
    void given_nullValue_whenCreate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Email(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    void given_blankValue_whenCreate_shouldGenerateException(String blank) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Email(blank));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "invalid@",
            "@gmail.com",
            "jhon doe@gmail.com",
            "jhon..doe@gmail.com",
            "jhon@gmail",
            "jhon@@gmail.com"
    })
    void given_invalidEmail_whenCreate_shouldGenerateException(String value) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Email(value));
    }

    @Test
    void given_email_whenCallToString_shouldReturnWrappedValue() {
        Assertions.assertThat(new Email("jhon.doe@gmail.com")).hasToString("jhon.doe@gmail.com");
    }

    @Test
    void given_sameValue_whenCompare_shouldBeEqual() {
        Assertions.assertThat(new Email("jhon.doe@gmail.com"))
                .isEqualTo(new Email("jhon.doe@gmail.com"))
                .hasSameHashCodeAs(new Email("jhon.doe@gmail.com"))
                .isNotEqualTo(new Email("jane.doe@gmail.com"));
    }
}
