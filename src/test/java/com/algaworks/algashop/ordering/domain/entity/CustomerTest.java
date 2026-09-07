package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    new Customer(
                            new CustomerId(),
                            new FullName("Jhon", "Doe"),
                            new BirthDate(LocalDate.of(1991, 7, 5)),
                            new Email("invalid"),
                            new Phone("478-256-2504"),
                            new Document("255-08-0578"),
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
                new BirthDate(LocalDate.of(1991, Month.AUGUST, 5)),
                new Email("jhon.doe@gmail.com"),
                new Phone("478-256-2504"),
                new Document("255-08-0578"),
                false,
                OffsetDateTime.now()
        );
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    customer.changeEmail(new Email("invalid"));
                });
    }

    @Test
    void given_unarchivedCustomer_whenArchived_shouldAnonymize() {
        var customer = new Customer(
                new CustomerId(),
                new FullName("Jhon", "Doe"),
                new BirthDate(LocalDate.of(1991, Month.AUGUST, 5)),
                new Email("jhon.doe@gmail.com"),
                new Phone("478-256-2504"),
                new Document("255-08-0578"),
                false,
                OffsetDateTime.now()
        );

        customer.archive();

        Assertions.assertWith(customer,
                c -> Assertions.assertThat(c.fullName()).isEqualTo(new FullName("Anonymous", "Anonymous")),
                c -> Assertions.assertThat(c.email()).isNotEqualTo(new Email("jhon.doe@gmail.com")),
                c -> Assertions.assertThat(c.phone()).isEqualTo(new Phone("000-000-0000")),
                c -> Assertions.assertThat(c.document()).isEqualTo(new Document("000-000-0000")),
                c -> Assertions.assertThat(c.birthDate()).isNull(),
                c -> Assertions.assertThat(c.isPromotionNotificationAllowed()).isFalse());
    }

    @Test
    void given_archivedCustomer_whenTryToUpdate_shouldGenerateException() {
        var customer = new Customer(
                new CustomerId(),
                new FullName("Anonymous", "Anonymous"),
                null,
                new Email("anonymous@anonimous.com"),
                new Phone("000-000-0000"),
                new Document("000-000-0000"),
                false,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                new LoyaltyPoints(10)
        );

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archive);
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changeEmail(new Email("update-email@test.com")));
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changePhone(new Phone("111-222-3333")));
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
                new BirthDate(LocalDate.of(1991, Month.AUGUST, 5)),
                new Email("jhon.doe@gmail.com"),
                new Phone("478-256-2504"),
                new Document("255-08-0578"),
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
                new BirthDate(LocalDate.of(1991, Month.AUGUST, 5)),
                new Email("jhon.doe@gmail.com"),
                new Phone("478-256-2504"),
                new Document("255-08-0578"),
                false,
                OffsetDateTime.now()
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(0)));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-1)));
    }

    @Test
    void given_validValueObjects_whenCreateCustomer_shouldBuildCustomer() {
        var customerId = new CustomerId();
        var registeredAt = OffsetDateTime.now();

        var customer = new Customer(
                customerId,
                new FullName("Jhon", "Doe"),
                new BirthDate(LocalDate.of(1991, Month.AUGUST, 5)),
                new Email("jhon.doe@gmail.com"),
                new Phone("478-256-2504"),
                new Document("255-08-0578"),
                false,
                registeredAt
        );

        Assertions.assertWith(customer,
                c -> Assertions.assertThat(c.id()).isEqualTo(customerId),
                c -> Assertions.assertThat(c.fullName()).isEqualTo(new FullName("Jhon", "Doe")),
                c -> Assertions.assertThat(c.birthDate()).isEqualTo(new BirthDate(LocalDate.of(1991, Month.AUGUST, 5))),
                c -> Assertions.assertThat(c.email()).hasToString("jhon.doe@gmail.com"),
                c -> Assertions.assertThat(c.phone()).hasToString("478-256-2504"),
                c -> Assertions.assertThat(c.document()).hasToString("255-08-0578"),
                c -> Assertions.assertThat(c.isPromotionNotificationAllowed()).isFalse(),
                c -> Assertions.assertThat(c.isArchived()).isFalse(),
                c -> Assertions.assertThat(c.registeredAt()).isEqualTo(registeredAt),
                c -> Assertions.assertThat(c.loyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO));
    }

    @Test
    void given_futureBirthDate_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Customer(
                        new CustomerId(),
                        new FullName("Jhon", "Doe"),
                        new BirthDate(LocalDate.now(ZoneId.of("UTC")).plusDays(1)),
                        new Email("jhon.doe@gmail.com"),
                        new Phone("478-256-2504"),
                        new Document("255-08-0578"),
                        false,
                        OffsetDateTime.now()
                ));
    }

    @Test
    void given_blankPhoneOrDocument_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Customer(
                        new CustomerId(),
                        new FullName("Jhon", "Doe"),
                        new BirthDate(LocalDate.of(1991, Month.AUGUST, 5)),
                        new Email("jhon.doe@gmail.com"),
                        new Phone(" "),
                        new Document("255-08-0578"),
                        false,
                        OffsetDateTime.now()
                ));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Customer(
                        new CustomerId(),
                        new FullName("Jhon", "Doe"),
                        new BirthDate(LocalDate.of(1991, Month.AUGUST, 5)),
                        new Email("jhon.doe@gmail.com"),
                        new Phone("478-256-2504"),
                        new Document(" "),
                        false,
                        OffsetDateTime.now()
                ));
    }

    @Test
    void given_nullPhoneValue_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Customer(
                        new CustomerId(),
                        new FullName("Jhon", "Doe"),
                        new BirthDate(LocalDate.of(1991, Month.AUGUST, 5)),
                        new Email("jhon.doe@gmail.com"),
                        new Phone(null),
                        new Document("255-08-0578"),
                        false,
                        OffsetDateTime.now()
                ));
    }
}
