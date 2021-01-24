package org.mediamarktsaturn.order.common.eventsource.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mediamarktsaturn.order.common.dto.OrderStatus;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusChangedEvent implements OrderEvent{
    public Long orderId;
    private OrderStatus newStatus;
}
