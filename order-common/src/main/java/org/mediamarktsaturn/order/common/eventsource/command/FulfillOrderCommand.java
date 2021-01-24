package org.mediamarktsaturn.order.common.eventsource.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class FulfillOrderCommand implements OrderCommand {
    private Long orderId;

    @Setter
    private boolean isPersisted = false;

    @Override
    public boolean isPersisted() {
        return false;
    }

    @Override
    public void setPersisted(boolean persisted) {

    }
}
