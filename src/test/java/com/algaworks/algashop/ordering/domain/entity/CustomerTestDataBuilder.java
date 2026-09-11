package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;

import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;

public class CustomerTestDataBuilder {

    private CustomerTestDataBuilder() {

    }

    static Customer.BrandNewCustomerBuild aBrandNewCustomer() {
        return Customer.brandNew()
                .fullName(new FullName("Jhon", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1991, Month.AUGUST, 5)))
                .email(new Email("jhon.doe@gmail.com"))
                .phone(new Phone("478-256-2504"))
                .document(new Document("255-08-0578"))
                .promotionNotificationAllowed(false)
                .address(anAddress());
    }

    static Customer.ExistingCustomerBuild anExistingCustomer() {
        return Customer.existing()
                .id(new CustomerId())
                .registeredAt(OffsetDateTime.now())
                .promotionNotificationAllowed(true)
                .loyaltyPoints(LoyaltyPoints.ZERO)
                .archived(false)
                .archivedAt(null)
                .fullName(new FullName("Jhon", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1991, Month.AUGUST, 5)))
                .email(new Email("jhon.doe@gmail.com"))
                .phone(new Phone("478-256-2504"))
                .document(new Document("255-08-0578"))
                .promotionNotificationAllowed(false)
                .address(anAddress());
    }

    static Customer.ExistingCustomerBuild existingAnonymizedCustomer() {
        return Customer.existing()
                .id(new CustomerId())
                .fullName(new FullName("Anonymous", "Anonymous"))
                .birthDate(null)
                .email(new Email("anonymous@anonimous.com"))
                .phone(new Phone("000-000-0000"))
                .document(new Document("000-000-0000"))
                .promotionNotificationAllowed(false)
                .archived(true)
                .registeredAt(OffsetDateTime.now())
                .archivedAt(OffsetDateTime.now())
                .loyaltyPoints(new LoyaltyPoints(10))
                .address(CustomerTestDataBuilder.anAddress());
    }

    static Address anAddress() {
        return Address.builder()
                .street("Bourbon Street")
                .complement("Apt. 114")
                .number("1134")
                .neighborhood("North Ville")
                .city("Yostfort")
                .state("South Carolina")
                .zipCode(new ZipCode("799610"))
                .build();
    }
}
