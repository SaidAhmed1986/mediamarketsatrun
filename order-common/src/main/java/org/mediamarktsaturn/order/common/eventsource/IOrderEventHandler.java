package org.mediamarktsaturn.order.common.eventsource;

import org.mediamarktsaturn.order.common.eventsource.event.OrderEvent;

public interface IOrderEventHandler {
    void handleEvent(OrderEvent event);
}
