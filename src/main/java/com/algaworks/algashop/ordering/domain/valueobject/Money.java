package com.algaworks.algashop.ordering.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal value) implements Comparable<Money> {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money(String value) {
        this(new BigDecimal(value));
    }

    public Money(BigDecimal value) {
        Objects.requireNonNull(value);
        value = value.setScale(2, ROUNDING_MODE);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public Money multiply(Quantity quantity) {
        Objects.requireNonNull(quantity);
        if (quantity.value() < 1) {
            throw new IllegalArgumentException();
        }
        return new Money(this.value().multiply(new BigDecimal(quantity.value())));
    }

    public Money add(Money other) {
        Objects.requireNonNull(other);
        return new Money(this.value().add(other.value()));
    }

    public Money divide(Money other) {
        Objects.requireNonNull(other);
        return new Money(this.value().divide(other.value(), 2, ROUNDING_MODE));
    }

    @Override
    public int compareTo(Money o) {
        return this.value().compareTo(o.value());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
