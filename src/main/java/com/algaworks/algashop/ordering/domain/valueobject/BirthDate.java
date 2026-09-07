package com.algaworks.algashop.ordering.domain.valueobject;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Objects;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST;

public record BirthDate(LocalDate value) {
    public BirthDate(LocalDate value) {
        Objects.requireNonNull(value);
        if (value.isAfter(LocalDate.now(ZoneId.of("UTC")))) {
            throw new IllegalArgumentException(VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
        }
        this.value = value;
    }

    public Integer age() {
        return Period.between(this.value(), LocalDate.now(ZoneId.of("UTC"))).getYears();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
