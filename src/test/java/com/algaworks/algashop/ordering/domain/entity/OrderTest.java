package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Test
    void shouldGenerate() {

        final var order = Order.draft(new CustomerId());

        assertThat(order).isNotNull();
    }

    @Test
    void shouldAddItem() {

        final var order = Order.draft(new CustomerId());
        final var product = ProductTestDataBuilder.aProduct_MousePad().build();
        final var productId = product.id();

        order.addItem(product, new Quantity(1));

        assertThat(order.items()).isNotEmpty();
        assertThat(order.items()).hasSize(1);
        final var orderItem = order.items().iterator().next();
        assertWith(orderItem,
                i -> assertThat(i.id()).isNotNull(),
                i -> assertThat(i.productName()).isEqualTo(new ProductName("Mouse Pad")),
                i -> assertThat(i.productId()).isEqualTo(productId),
                i -> assertThat(i.price()).isEqualTo(new Money("100")),
                i -> assertThat(i.quantity()).isEqualTo(new Quantity(1))
        );
    }

    @Test
    void shouldGenerateExceptionWhenTryToChangeItemSet() {

        final var order = Order.draft(new CustomerId());
        order.addItem(
                ProductTestDataBuilder.aProduct_MousePad().build(),
                new Quantity(1)
        );

        Set<OrderItem> items = order.items();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(items::clear);
    }

    @Test
    void shouldCalculateTotals() {

        final var order = Order.draft(new CustomerId());
        order.addItem(
                ProductTestDataBuilder.aProduct_MousePad().build(),
                new Quantity(2)
        );
        order.addItem(
                ProductTestDataBuilder.aProduct_RamMemory().build(),
                new Quantity(1)
        );

        assertThat(order.totalAmount()).isEqualTo(new Money("400"));
        assertThat(order.totalItems()).isEqualTo(new Quantity(3));
    }

    @Test
    void givenDraftOrder_whenPlace_shouldChangeToPlaced() {
        Order order = OrderTestDataBuilder.anOrder().build();
        order.place();

        assertThat(order.isPlaced()).isTrue();
    }

    @Test
    void givenPlacedOrder_whenTryToPlace_shouldGenerateException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::place);
    }

    @Test
    void givenOrder_whenChangePayment_shouldAllowChange() {
        Order order = Order.draft(new CustomerId());
        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);

        assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    void givenDraftOrder_whenChangeBillingInfo_shouldAllowChange() {
        Address address = Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .city("Montfort")
                .state("Manitoba")
                .zipCode(new ZipCode("A1BC2D"))
                .build();
        BillingInfo billingInfo = BillingInfo.builder()
                .address(address)
                .document(new Document("225-09-1992"))
                .phone(new Phone("844-514-7832"))
                .fullName(new FullName("John", "Doe"))
                .build();
        Order order = Order.draft(new CustomerId());

        order.changeBillingInfo(billingInfo);

        BillingInfo expectedBillingInfo = BillingInfo.builder()
                .address(address)
                .document(new Document("225-09-1992"))
                .phone(new Phone("844-514-7832"))
                .fullName(new FullName("John", "Doe"))
                .build();
        assertThat(order.billing()).isEqualTo(expectedBillingInfo);
    }

    @Test
    void givenDraftOrder_whenChangeShippingInfo_shouldAllowChange() {
        Address address = Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .city("Montfort")
                .state("Manitoba")
                .zipCode(new ZipCode("A1BC2D"))
                .build();
        ShippingInfo shippingInfo = ShippingInfo.builder()
                .address(address)
                .document(new Document("225-09-1992"))
                .phone(new Phone("844-514-7832"))
                .fullName(new FullName("John", "Doe"))
                .build();
        Order order = Order.draft(new CustomerId());
        Money shippingCost = Money.ZERO;
        LocalDate expectedDeliveryDate = LocalDate.now().plusDays(1);

        order.changeShippingInfo(shippingInfo, shippingCost, expectedDeliveryDate);

        ShippingInfo expectedShippingInfo = ShippingInfo.builder()
                .address(address)
                .document(new Document("225-09-1992"))
                .phone(new Phone("844-514-7832"))
                .fullName(new FullName("John", "Doe"))
                .build();
        assertWith(order,
                o -> assertThat(o.shipping()).isEqualTo(expectedShippingInfo),
                o -> assertThat(o.shippingCost()).isEqualTo(shippingCost),
                o -> assertThat(o.expectedDeliveryDate()).isEqualTo(expectedDeliveryDate)
                );
    }

    @Test
    void givenDraftOrderAndDeliveryDateIsInThePast_whenChangeShippingInfo_shouldNotAllowChange() {
        Address address = Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .city("Montfort")
                .state("Manitoba")
                .zipCode(new ZipCode("A1BC2D"))
                .build();
        ShippingInfo shippingInfo = ShippingInfo.builder()
                .address(address)
                .document(new Document("225-09-1992"))
                .phone(new Phone("844-514-7832"))
                .fullName(new FullName("John", "Doe"))
                .build();
        Order order = Order.draft(new CustomerId());
        Money shippingCost = Money.ZERO;
        LocalDate expectedDeliveryDate = LocalDate.now().minusDays(2);


        assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(() -> order.changeShippingInfo(shippingInfo, shippingCost, expectedDeliveryDate));
    }

    @Test
    void givenPlacedOrder_whenMarkedAsPaid_shouldChangeToPaid() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        order.markAsPaid();

        assertThat(order.isPaid()).isTrue();
        assertThat(order.paidAt()).isNotNull();

    }

    @Test
    void givenDraftOrder_whenChangeItem_shouldRecalculate() {
        Order order = Order.draft(new CustomerId());
        order.addItem(
                ProductTestDataBuilder.aProduct().build(),
                new Quantity(3)
        );
        OrderItem orderItem = order.items().iterator().next();

        order.changeItemQuantity(orderItem.id(), new Quantity(5));

        assertWith(order,
                o -> assertThat(o.totalAmount()).isEqualTo(new Money("15000")),
                o-> assertThat(o.totalItems()).isEqualTo(new Quantity(5))
        );
    }
}