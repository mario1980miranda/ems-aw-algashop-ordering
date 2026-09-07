package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DocumentTest {

    @Test
    void given_validValue_whenCreate_shouldKeepValue() {
        Document document = new Document("255-08-0578");

        Assertions.assertThat(document.value()).isEqualTo("255-08-0578");
    }

    @Test
    void given_nullValue_whenCreate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Document(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void given_blankValue_whenCreate_shouldGenerateException(String blank) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Document(blank));
    }

    @Test
    void given_document_whenCallToString_shouldReturnWrappedValue() {
        Assertions.assertThat(new Document("255-08-0578")).hasToString("255-08-0578");
    }

    @Test
    void given_sameValue_whenCompare_shouldBeEqual() {
        Assertions.assertThat(new Document("255-08-0578"))
                .isEqualTo(new Document("255-08-0578"))
                .hasSameHashCodeAs(new Document("255-08-0578"))
                .isNotEqualTo(new Document("000-000-0000"));
    }
}
