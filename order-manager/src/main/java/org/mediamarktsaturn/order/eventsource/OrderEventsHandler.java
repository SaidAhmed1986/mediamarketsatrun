package org.mediamarktsaturn.order.eventsource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mediamarktsaturn.order.common.eventsource.IOrderEventHandler;
import org.mediamarktsaturn.order.common.messaging.OrderEventsPublisher;
import org.mediamarktsaturn.order.common.eventsource.event.OrderClosedEvent;
import org.mediamarktsaturn.order.common.eventsource.event.OrderEvent;
import org.mediamarktsaturn.order.common.eventsource.event.OrderFulfilledEvent;
import org.mediamarktsaturn.order.common.eventsource.event.OrderPaidEvent;
import org.mediamarktsaturn.order.common.eventsource.event.OrderStatusChangedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.mediamarktsaturn.order.common.eventsource.event.OrderFulfilledEvent.FulFilamentStatus.FAILED;
import static org.mediamarktsaturn.order.common.eventsource.event.OrderFulfilledEvent.FulFilamentStatus.SUCCESS;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventsHandler implements IOrderEventHandler {

    private final OrderEventsPublisher eventsProducer;

    private Map<Class<? extends OrderEvent>, Consumer<OrderEvent>> eventHandlers = new HashMap<>();

    @PostConstruct
    void init(){
        eventHandlers.put(OrderStatusChangedEvent.class, (event)-> handleOrderStatusChangedEvent((OrderStatusChangedEvent) event));
    }

    public void handleEvent(OrderEvent event){
        Optional.ofNullable(eventHandlers.get(event.getClass()))
                .ifPresent((handlerConsumer)-> handlerConsumer.accept(event));
    }

    private void handleOrderStatusChangedEvent(OrderStatusChangedEvent evt){
        OrderEvent event = null;
        switch (evt.getNewStatus()) {
            case PAID:
                event = new OrderPaidEvent(evt.getOrderId());
                break;
            case CLOSED:
                event = new OrderClosedEvent(evt.getOrderId());
                break;
            case FULFILLMENT:
                event = new OrderFulfilledEvent(evt.getOrderId(), SUCCESS);
                break;
            case FULFILLMENT_FAILED:
                event = new OrderFulfilledEvent(evt.getOrderId(), FAILED);
                break;
        }
        Optional.ofNullable(event).ifPresent((newEvent)-> eventsProducer.publishEvent(newEvent));
    }
}
