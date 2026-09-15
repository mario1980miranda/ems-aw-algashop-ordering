package com.algaworks.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;

@Builder(toBuilder = true)
public record Shipping(
        Money cost,
        LocalDate expectedDeliveryDate,
        Recipient recipient,
        Address address
) {
    public Shipping {
        Objects.requireNonNull(cost);
        Objects.requireNonNull(expectedDeliveryDate);
        Objects.requireNonNull(recipient);
        Objects.requireNonNull(address);
    }
}
