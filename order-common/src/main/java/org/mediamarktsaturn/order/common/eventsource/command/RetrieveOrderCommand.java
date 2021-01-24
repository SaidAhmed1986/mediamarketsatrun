package org.mediamarktsaturn.order.common.eventsource.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public final class RetrieveOrderCommand implements OrderCommand {
    private final Long orderId;
    @Setter
    private boolean isPersisted = true;

    @Override
    public String getPersistenceKey() {
        return OrderCommand.super.getPersistenceKey() + ":" + orderId;
    }
}