package com.algaworks.algashop.ordering.domain.valueobject;

import java.util.Objects;

public record Document(String value) {
    public Document(String value) {
        Objects.requireNonNull(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
