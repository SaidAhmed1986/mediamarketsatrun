package org.mediamarktsaturn.order.common.eventsource.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderClosedEvent implements OrderEvent {
    public Long orderId;
}
