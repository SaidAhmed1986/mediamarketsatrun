package org.mediamarktsaturn.order.order.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import lombok.RequiredArgsConstructor;
import org.mediamarktsaturn.order.order.dto.OrderDetailsDto;
import org.mediamarktsaturn.order.order.eventsource.command.CreateOrderCommand;
import org.mediamarktsaturn.order.order.eventsource.command.GetOrderPaymentInfoCommand;
import org.mediamarktsaturn.order.order.eventsource.command.GetOrderShippingInfoCommand;
import org.mediamarktsaturn.order.order.eventsource.command.RetrieveOrderCommand;
import org.mediamarktsaturn.order.order.eventsource.command.UpdateOrderStatusCommand;
import org.mediamarktsaturn.order.order.eventsource.event.OrderCreatedEvent;
import org.mediamarktsaturn.order.order.eventsource.event.OrderStatusChangedEvent;
import org.mediamarktsaturn.order.order.exception.OrderManagerException;
import org.mediamarktsaturn.order.order.messaging.OrderEventsPublisher;
import org.mediamarktsaturn.order.order.service.OrderService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderManagerActor extends AbstractLoggingActor {
    private final OrderService orderService;
    private final OrderEventsPublisher kafkaProducer;


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateOrderCommand.class, this::handleCreateOrderCommand)
                .match(RetrieveOrderCommand.class, this::handleRetrieveOrderCommand)
                .match(UpdateOrderStatusCommand.class, this::handleUpdateOrderStatusCommand)
                .match(GetOrderPaymentInfoCommand.class, this::handleGetOrderPaymentInfoCommand)
                .match(GetOrderShippingInfoCommand.class, this::handleGetOrderShippingInfoCommand)
                .build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(
                10,
                Duration.ofMinutes(1),
                DeciderBuilder.match(OrderManagerException.class, e -> SupervisorStrategy.resume())
                        .matchAny(o -> SupervisorStrategy.escalate())
                        .build()
        );
    }

    private  void handleGetOrderShippingInfoCommand(GetOrderShippingInfoCommand command) {
        sender().tell(Optional.ofNullable(orderService.getOrder(command.getOrderId()))
                .map(OrderDetailsDto::getShippingInfo), getSelf());
    }

    private void handleGetOrderPaymentInfoCommand(GetOrderPaymentInfoCommand command) {
       sender().tell(Optional.ofNullable(orderService.getOrder(command.getOrderId()))
               .map(OrderDetailsDto::getPaymentInfo), getSelf());
    }

    private void handleUpdateOrderStatusCommand(UpdateOrderStatusCommand updateCommand) {
        sender().tell(Optional.ofNullable(orderService.updateOrderStatus(updateCommand.getOrderId(), updateCommand.getNewStatus())), getSelf());
        kafkaProducer.publishEvent(new OrderStatusChangedEvent(updateCommand.getOrderId(), updateCommand.getNewStatus()));
    }

    private void handleRetrieveOrderCommand(RetrieveOrderCommand retrieveOrderCommand) {
        sender().tell(Optional.ofNullable(orderService.getOrder(retrieveOrderCommand.getOrderId())), getSelf());
    }

    private void handleCreateOrderCommand(CreateOrderCommand createOrderCommand) {
        OrderDetailsDto createdOrder = orderService.createOrder(createOrderCommand.getOrder());
        sender().tell(createdOrder, getSelf());
        kafkaProducer.publishEvent(new OrderCreatedEvent(createdOrder.getId()));
    }

}
