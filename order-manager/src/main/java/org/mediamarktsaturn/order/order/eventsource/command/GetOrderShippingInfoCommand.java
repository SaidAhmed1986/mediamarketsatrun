package org.mediamarktsaturn.order.order.eventsource.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public final class GetOrderShippingInfoCommand implements OrderCommand {
    private final Long orderId;
    @Setter
    private boolean isPersisted = false;

    @Override
    public String getPersistenceKey() {
        return OrderCommand.super.getPersistenceKey() + ":" + orderId;
    }
}