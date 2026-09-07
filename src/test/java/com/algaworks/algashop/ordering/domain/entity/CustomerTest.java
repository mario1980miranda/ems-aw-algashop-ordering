package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.LoyaltyPoints;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    new Customer(
                            new CustomerId(),
                            new FullName("Jhon", "Doe"),
                            LocalDate.of(1991, 7, 5),
                            "invalid",
                            "478-256-2504",
                            "255-08-0578",
                            false,
                            OffsetDateTime.now()
                    );
                });
    }

    @Test
    void given_invalidEmail_whenTryUpdateCustomerEmail_shouldGenerateException() {
        var customer = new Customer(
                new CustomerId(),
                new FullName("Jhon", "Doe"),
                LocalDate.of(1991, Month.AUGUST, 5),
                "jhon.doe@gmail.com",
                "478-256-2504",
                "255-08-0578",
                false,
                OffsetDateTime.now()
        );
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    customer.changeEmail("invalid");
                });
    }

    @Test
    void given_unarchivedCustomer_whenArchived_shouldAnonymize() {
        var customer = new Customer(
                new CustomerId(),
                new FullName("Jhon", "Doe"),
                LocalDate.of(1991, Month.AUGUST, 5),
                "jhon.doe@gmail.com",
                "478-256-2504",
                "255-08-0578",
                false,
                OffsetDateTime.now()
        );

        customer.archive();

        Assertions.assertWith(customer,
                c -> Assertions.assertThat(c.fullName()).isEqualTo(new FullName("Anonymous", "Anonymous")),
                c -> Assertions.assertThat(c.email()).isNotEqualTo("jhon.doe@gmail.com"),
                c -> Assertions.assertThat(c.phone()).isEqualTo("000-000-0000"),
                c -> Assertions.assertThat(c.document()).isEqualTo("000-000-0000"),
                c -> Assertions.assertThat(c.birthDate()).isNull(),
                c -> Assertions.assertThat(c.isPromotionNotificationAllowed()).isFalse());
    }

    @Test
    void given_archivedCustomer_whenTryToUpdate_shouldGenerateException() {
        var customer = new Customer(
                new CustomerId(),
                new FullName("Anonymous", "Anonymous"),
                null,
                "anonymous@anonimous.com",
                "000-000-0000",
                "000-000-0000",
                false,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                new LoyaltyPoints(10)
        );

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archive);
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changeEmail("update-email@test.com"));
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changePhone("111-222-3333"));
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::enableNotifications);
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::disableNotifications);
    }

    @Test
    void given_brandNewCustomer_whenAddLoayltyPoints_shouldSumPoints() {
        var customer = new Customer(
                new CustomerId(),
                new FullName("Jhon", "Doe"),
                LocalDate.of(1991, Month.AUGUST, 5),
                "jhon.doe@gmail.com",
                "478-256-2504",
                "255-08-0578",
                false,
                OffsetDateTime.now()
        );

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoayltyPoints_shouldGenerateException() {
        var customer = new Customer(
                new CustomerId(),
                new FullName("Jhon", "Doe"),
                LocalDate.of(1991, Month.AUGUST, 5),
                "jhon.doe@gmail.com",
                "478-256-2504",
                "255-08-0578",
                false,
                OffsetDateTime.now()
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(0)));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-1)));
    }
}