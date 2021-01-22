package org.mediamarktsaturn.order.order.dto;

import java.util.Optional;

public enum OrderStatus {
    RECEIVED, CREATED, PAID, FULFILLMENT, CLOSED, FULFILLMENT_FAILED;

    public static Optional<OrderStatus> fromString(String status) {
        OrderStatus matchedStatus = null;
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.name().equalsIgnoreCase(status)) {
                matchedStatus = orderStatus;break;
            }
        }
        return Optional.ofNullable(matchedStatus);
    }
}
