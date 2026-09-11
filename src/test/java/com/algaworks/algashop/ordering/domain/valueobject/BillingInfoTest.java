package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class BillingInfoTest {

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
        BillingInfo billingInfo = BillingInfo.builder()
                .fullName(new FullName("John", "Doe"))
                .document(new Document("255-08-0578"))
                .phone(new Phone("1191125-5555"))
                .address(anAddress())
                .build();

        assertThat(billingInfo.fullName()).hasToString("John Doe");
        assertThat(billingInfo.address()).isEqualTo(anAddress());
    }

    @Test
    void shouldNotAllowNullFields() {
        assertThatNullPointerException().isThrownBy(() -> BillingInfo.builder()
                .fullName(new FullName("John", "Doe"))
                .document(new Document("255-08-0578"))
                .phone(new Phone("1191125-5555"))
                .build());
    }
}
