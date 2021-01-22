package org.mediamarktsaturn.order.order.eventsource.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderFulfilledEvent implements OrderEvent{
    private Long orderId;
    private FulFilamentStatus fulFilamentStatus;

    public enum FulFilamentStatus {SUCCESS, FAILED}
}
