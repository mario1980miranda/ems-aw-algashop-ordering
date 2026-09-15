package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.*;

public class OrderCannotBePlacedException extends DomainException {

    private OrderCannotBePlacedException(String message) {
        super(message);
    }

    public static OrderCannotBePlacedException noItems(OrderId orderId) {
        return new OrderCannotBePlacedException(
                String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS, orderId)
        );
    }

    public static OrderCannotBePlacedException noShippingInfo(OrderId orderId) {
        return new OrderCannotBePlacedException(
                String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO, orderId)
        );
    }

    public static OrderCannotBePlacedException noBillingInfo(OrderId orderId) {
        return new OrderCannotBePlacedException(
                String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO, orderId)
        );
    }

    public static OrderCannotBePlacedException invalidShippingCost(OrderId orderId) {
        return new OrderCannotBePlacedException(
                String.format(ERROR_ORDER_CANNOT_BE_PLACED_INVALID_SHIPPING_COST, orderId)
        );
    }

    public static OrderCannotBePlacedException invalidExpectedDeliveryDate(OrderId orderId) {
        return new OrderCannotBePlacedException(
                String.format(ERROR_ORDER_CANNOT_BE_PLACED_INVALID_EXPECTED_DATE, orderId)
        );
    }

    public static OrderCannotBePlacedException noPaymentMethod(OrderId orderId) {
        return new OrderCannotBePlacedException(
                String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD, orderId)
        );
    }
}
