package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class ShippingTest {

    private static Address anAddress() {
        return Address.builder()
                .street("Bourbon Street")
                .number("1134")
                .neighborhood("North Ville")
                .city("York")
                .state("South California")
                .zipCode(new ZipCode("123456"))
                .complement("Apt. 114")
                .build();
    }

    @Test
    void shouldGenerateWithValidData() {
        Shipping shipping = Shipping.builder()
                .cost(new Money("10.00"))
                .expectedDeliveryDate(LocalDate.now(ZoneId.systemDefault()).plusWeeks(2))
                .recipient(Recipient.builder()
                        .fullName(new FullName("John", "Doe"))
                        .document(new Document("255-08-0578"))
                        .phone(new Phone("1191125-5555"))
                        .build())
                .address(anAddress())
                .build();

        assertThat(shipping.recipient().fullName()).hasToString("John Doe");
        assertThat(shipping.address()).isEqualTo(anAddress());
    }
}
