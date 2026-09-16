package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.utility.validator.FieldValidations;

public record ProductName(String value) {

    public ProductName {
        FieldValidations.requiresNonBlank(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
