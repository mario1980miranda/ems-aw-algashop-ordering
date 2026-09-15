package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;

import java.time.LocalDate;
import java.time.ZoneId;

public class OrderTestDataBuilder {

    private CustomerId customerId = new CustomerId();

    private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;

    private Shipping shipping = aShipping();

    private Billing billing = aBilling();

    private boolean withItems = true;

    private OrderStatus status = OrderStatus.DRAFT;

    private OrderTestDataBuilder() {
    }

    public static OrderTestDataBuilder anOrder() {
        return new OrderTestDataBuilder();
    }

    public Order build() {
        Order order = Order.draft(customerId);
        order.changeShipping(shipping);
        order.changeBillingInfo(billing);
        order.changePaymentMethod(paymentMethod);

        if (withItems) {
            order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
            order.addItem(ProductTestDataBuilder.aProduct_RamMemory().build(), new Quantity(1));
        }

        switch (this.status) {
            case DRAFT -> {

            }
            case PLACED -> {
                order.place();
            }
            case PAID -> {
                order.place();
                order.markAsPaid();
            }
            case READY -> {

            }
            case CANCELED -> {

            }
        }

        return order;
    }

    public static Shipping aShipping() {
        return Shipping.builder()
                .cost(new Money("10"))
                .expectedDeliveryDate(LocalDate.now(ZoneId.systemDefault()))
                .address(anAddress())
                .recipient(Recipient.builder()
                        .document(new Document("225-09-1992"))
                        .phone(new Phone("844-514-7832"))
                        .fullName(new FullName("John", "Doe"))
                        .build())
                .build();
    }

    public static Billing aBilling() {
        return Billing.builder()
                .address(anAddress())
                .document(new Document("225-09-1992"))
                .phone(new Phone("844-514-7832"))
                .fullName(new FullName("John", "Doe"))
                .email(new Email("email@email.com"))
                .build();
    }

    public static Address anAddress() {
        return Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .city("Montfort")
                .state("Manitoba")
                .zipCode(new ZipCode("A1BC2D"))
                .build();
    }

    public static Shipping aShippingAlt() {
        return Shipping.builder()
                .cost(new Money("20.00"))
                .expectedDeliveryDate(LocalDate.now(ZoneId.systemDefault()).plusWeeks(2))
                .address(anAddress())
                .recipient(Recipient.builder()
                        .document(new Document("111-222-3333"))
                        .phone(new Phone("111-111-1111"))
                        .fullName(new FullName("Mary", "Jones"))
                        .build())
                .build();
    }

    public static Address anAddressAlt() {
        return Address.builder()
                .street("Sansone Street")
                .number("875")
                .neighborhood("Sansone")
                .city("San Francisco")
                .state("California")
                .zipCode(new ZipCode("E3FG4H"))
                .build();
    }

    public OrderTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderTestDataBuilder paymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderTestDataBuilder shipping(Shipping shipping) {
        this.shipping = shipping;
        return this;
    }

    public OrderTestDataBuilder billing(Billing billingInfo) {
        this.billing = billingInfo;
        return this;
    }

    public OrderTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public OrderTestDataBuilder status(OrderStatus status) {
        this.status = status;
        return this;
    }
}
