package org.mediamarktsaturn.order.common.eventsource.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mediamarktsaturn.order.common.eventsource.command.OrderCommand;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public final class GetOrderPaymentInfoCommand implements OrderCommand {
    private final Long orderId;
    @Setter
    private boolean isPersisted = true;

    @Override
    public String getPersistenceKey() {
        return OrderCommand.super.getPersistenceKey() + ":" + orderId;
    }

}