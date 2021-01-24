package org.mediamarktsaturn.order.fulfillment.actor;

import akka.actor.AbstractLoggingActor;
import lombok.RequiredArgsConstructor;
import org.mediamarktsaturn.order.common.eventsource.command.FulfillOrderCommand;
import org.mediamarktsaturn.order.fulfillment.service.IOrderFulfillmentService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderFulfillmentActor extends AbstractLoggingActor  {
    private final IOrderFulfillmentService fulfillmentService;
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(FulfillOrderCommand.class, command -> fulfillmentService.fulFillOrder(command.getOrderId()))
                .build();
    }
}
