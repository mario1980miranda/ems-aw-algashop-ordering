package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    Customer.brandNew(
                            new FullName("Jhon", "Doe"),
                            new BirthDate(LocalDate.of(1991, 7, 5)),
                            new Email("invalid"),
                            new Phone("478-256-2504"),
                            new Document("255-08-0578"),
                            false,
                            Address.builder()
                                    .street("Boulevard Lebourneuf")
                                    .number("1255")
                                    .neighborhood("Centre Ville")
                                    .city("Québec")
                                    .state("QC")
                                    .zipCode(new ZipCode("G1S3M7"))
                                    .build()
                    );
                });
    }

    @Test
    void given_invalidEmail_whenTryUpdateCustomerEmail_shouldGenerateException() {
        var customer = Customer.brandNew(
                new FullName("Jhon", "Doe"),
                new BirthDate(LocalDate.of(1991, 7, 5)),
                new Email("valid@email.com"),
                new Phone("478-256-2504"),
                new Document("255-08-0578"),
                false,
                Address.builder()
                        .street("Boulevard Lebourneuf")
                        .number("1255")
                        .neighborhood("Centre Ville")
                        .city("Québec")
                        .state("QC")
                        .zipCode(new ZipCode("G1S3M7"))
                        .build()
        );
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    customer.changeEmail(new Email("invalid"));
                });
    }

    @Test
    void given_unarchivedCustomer_whenArchived_shouldAnonymize() {
        var customer = Customer.brandNew(
                new FullName("Jhon", "Doe"),
                new BirthDate(LocalDate.of(1991, 7, 5)),
                new Email("valid@email.com"),
                new Phone("478-256-2504"),
                new Document("255-08-0578"),
                false,
                Address.builder()
                        .street("Boulevard Lebourneuf")
                        .number("1255")
                        .neighborhood("Centre Ville")
                        .city("Québec")
                        .state("QC")
                        .zipCode(new ZipCode("G1S3M7"))
                        .build()
        );

        customer.archive();

        Assertions.assertWith(customer,
                c -> assertThat(c.fullName()).isEqualTo(new FullName("Anonymous", "Anonymous")),
                c -> assertThat(c.email()).isNotEqualTo(new Email("jhon.doe@gmail.com")),
                c -> assertThat(c.phone()).isEqualTo(new Phone("000-000-0000")),
                c -> assertThat(c.document()).isEqualTo(new Document("000-000-0000")),
                c -> assertThat(c.birthDate()).isNull(),
                c -> assertThat(c.isPromotionNotificationAllowed()).isFalse(),
                c -> assertThat(c.address()).isEqualTo(Address.builder()
                        .street("Boulevard Lebourneuf")
                        .number("Anonymized")
                        .neighborhood("Centre Ville")
                        .city("Québec")
                        .state("QC")
                        .zipCode(new ZipCode("G1S3M7"))
                                .complement(null)
                        .build()));
    }

    @Test
    void given_archivedCustomer_whenTryToUpdate_shouldGenerateException() {
        var customer = Customer.existing(
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
                new LoyaltyPoints(10),
                Address.builder()
                        .street("Boulevard Lebourneuf")
                        .number("1255")
                        .neighborhood("Centre Ville")
                        .city("Québec")
                        .state("QC")
                        .zipCode(new ZipCode("G1S3M7"))
                        .build()
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
        var customer = Customer.brandNew(
                new FullName("Jhon", "Doe"),
                new BirthDate(LocalDate.of(1991, 7, 5)),
                new Email("valid@email.com"),
                new Phone("478-256-2504"),
                new Document("255-08-0578"),
                false,
                Address.builder()
                        .street("Boulevard Lebourneuf")
                        .number("1255")
                        .neighborhood("Centre Ville")
                        .city("Québec")
                        .state("QC")
                        .zipCode(new ZipCode("G1S3M7"))
                        .build()
        );

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));

        assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoayltyPoints_shouldGenerateException() {
        var customer = Customer.brandNew(
                new FullName("Jhon", "Doe"),
                new BirthDate(LocalDate.of(1991, 7, 5)),
                new Email("valid@email.com"),
                new Phone("478-256-2504"),
                new Document("255-08-0578"),
                false,
                Address.builder()
                        .street("Boulevard Lebourneuf")
                        .number("1255")
                        .neighborhood("Centre Ville")
                        .city("Québec")
                        .state("QC")
                        .zipCode(new ZipCode("G1S3M7"))
                        .build()
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(0)));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-1)));
    }

    @Test
    void given_validValueObjects_whenCreateCustomer_shouldBuildCustomer() {
        var registeredAt = OffsetDateTime.now();

        var customer = Customer.brandNew(
                new FullName("Jhon", "Doe"),
                new BirthDate(LocalDate.of(1991, Month.AUGUST, 5)),
                new Email("jhon.doe@gmail.com"),
                new Phone("478-256-2504"),
                new Document("255-08-0578"),
                false,
                Address.builder()
                        .street("Boulevard Lebourneuf")
                        .number("1255")
                        .neighborhood("Centre Ville")
                        .city("Québec")
                        .state("QC")
                        .zipCode(new ZipCode("G1S3M7"))
                        .build()
        );

        Assertions.assertWith(customer,
                c -> assertThat(c.fullName()).isEqualTo(new FullName("Jhon", "Doe")),
                c -> assertThat(c.birthDate()).isEqualTo(new BirthDate(LocalDate.of(1991, Month.AUGUST, 5))),
                c -> assertThat(c.email()).hasToString("jhon.doe@gmail.com"),
                c -> assertThat(c.phone()).hasToString("478-256-2504"),
                c -> assertThat(c.document()).hasToString("255-08-0578"),
                c -> assertThat(c.isPromotionNotificationAllowed()).isFalse(),
                c -> assertThat(c.isArchived()).isFalse(),
                c -> assertThat(c.loyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO));
    }

    @Test
    void given_futureBirthDate_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Customer.brandNew(
                        new FullName("Jhon", "Doe"),
                        new BirthDate(LocalDate.of(1991, 7, 5)),
                        new Email("invalid"),
                        new Phone("478-256-2504"),
                        new Document("255-08-0578"),
                        false,
                        Address.builder()
                                .street("Boulevard Lebourneuf")
                                .number("1255")
                                .neighborhood("Centre Ville")
                                .city("Québec")
                                .state("QC")
                                .zipCode(new ZipCode("G1S3M7"))
                                .build()
                ));
    }

    @Test
    void given_blankPhoneOrDocument_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Customer.brandNew(
                        new FullName("Jhon", "Doe"),
                        new BirthDate(LocalDate.of(1991, 7, 5)),
                        new Email("invalid"),
                        new Phone("478-256-2504"),
                        new Document("255-08-0578"),
                        false,
                        Address.builder()
                                .street("Boulevard Lebourneuf")
                                .number("1255")
                                .neighborhood("Centre Ville")
                                .city("Québec")
                                .state("QC")
                                .zipCode(new ZipCode("G1S3M7"))
                                .build()
                ));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Customer.brandNew(
                        new FullName("Jhon", "Doe"),
                        new BirthDate(LocalDate.of(1991, 7, 5)),
                        new Email("invalid"),
                        new Phone("478-256-2504"),
                        new Document("255-08-0578"),
                        false,
                        Address.builder()
                                .street("Boulevard Lebourneuf")
                                .number("1255")
                                .neighborhood("Centre Ville")
                                .city("Québec")
                                .state("QC")
                                .zipCode(new ZipCode("G1S3M7"))
                                .build()
                ));
    }

    @Test
    void given_nullPhoneValue_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Customer.brandNew(
                        new FullName("Jhon", "Doe"),
                        new BirthDate(LocalDate.of(1991, 7, 5)),
                        new Email("valid@email.com"),
                        null,
                        new Document("255-08-0578"),
                        false,
                        Address.builder()
                                .street("Boulevard Lebourneuf")
                                .number("1255")
                                .neighborhood("Centre Ville")
                                .city("Québec")
                                .state("QC")
                                .zipCode(new ZipCode("G1S3M7"))
                                .build()
                ));
    }
}
