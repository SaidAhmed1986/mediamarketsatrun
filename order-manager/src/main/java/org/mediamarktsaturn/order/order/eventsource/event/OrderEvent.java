package org.mediamarktsaturn.order.order.eventsource.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface OrderEvent extends Serializable {
    Long getOrderId();
}
