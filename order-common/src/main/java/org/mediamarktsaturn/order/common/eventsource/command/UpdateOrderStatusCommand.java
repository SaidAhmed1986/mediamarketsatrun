package org.mediamarktsaturn.order.common.eventsource.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mediamarktsaturn.order.common.dto.OrderStatus;

@Getter
@RequiredArgsConstructor
public final class UpdateOrderStatusCommand implements OrderCommand {
    private final Long orderId;
    private final OrderStatus newStatus;

    @Setter
    private boolean isPersisted = false;

    @Override
    public String getPersistenceKey() {
        return OrderCommand.super.getPersistenceKey() + ":" + orderId + ":" + newStatus;
    }
}