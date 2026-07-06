package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.utility.IdGenerator;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Map;
import java.util.UUID;

public class CustomerTest {

    @Test
    void testingCustomer() {

        Customer customer = new Customer(
                IdGenerator.generateTimeBasedUUID(),
                "Jhon Doe",
                LocalDate.of(1991, Month.JULY,5),
                "jhon.doe@email.com",
                "255-852-1397",
                "399-555-7894",
                true,
                OffsetDateTime.of(2025,7,5,15,45,0, 0, ZoneOffset.UTC)
        );

        customer.addLoyaltyPoints(15);

        System.out.println(customer.id());
    }
}
