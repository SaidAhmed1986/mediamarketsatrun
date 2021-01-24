package org.mediamarktsaturn.order.fulfillment.eventsource;

import akka.actor.ActorRef;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mediamarktsaturn.order.common.eventsource.IOrderEventHandler;
import org.mediamarktsaturn.order.common.eventsource.command.FulfillOrderCommand;
import org.mediamarktsaturn.order.common.eventsource.event.OrderEvent;
import org.mediamarktsaturn.order.common.eventsource.event.OrderPaidEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventsHandler implements IOrderEventHandler {

    final ActorRef orderFulfillmentActor ;

    private Map<Class<? extends OrderEvent>, Consumer<OrderEvent>> eventHandlers = new HashMap<>();

    @PostConstruct
    void init(){
        eventHandlers.put(OrderPaidEvent.class, (event)-> handleOrderPaidEvent((OrderPaidEvent) event));
    }

    public void handleEvent(OrderEvent event){
        Optional.ofNullable(eventHandlers.get(event.getClass()))
                .ifPresent((handlerConsumer)-> handlerConsumer.accept(event));
    }

    private void handleOrderPaidEvent(OrderPaidEvent event) {
        orderFulfillmentActor.tell(new FulfillOrderCommand(event.getOrderId(), false), ActorRef.noSender());
    }

}
