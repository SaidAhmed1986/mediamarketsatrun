package org.mediamarktsaturn.order.order.eventsource;

import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.pattern.StatusReply;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EffectBuilder;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mediamarktsaturn.order.order.dto.OrderDetailsDto;
import org.mediamarktsaturn.order.order.dto.OrderStatus;
import org.mediamarktsaturn.order.order.eventsource.command.CreateOrderCommand;
import org.mediamarktsaturn.order.order.eventsource.command.OrderCommand;
import org.mediamarktsaturn.order.order.eventsource.command.RetrieveOrderCommand;
import org.mediamarktsaturn.order.order.eventsource.command.UpdateOrderStatusCommand;
import org.mediamarktsaturn.order.order.eventsource.event.OrderClosedEvent;
import org.mediamarktsaturn.order.order.eventsource.event.OrderCreatedEvent;
import org.mediamarktsaturn.order.order.eventsource.event.OrderEvent;
import org.mediamarktsaturn.order.order.eventsource.event.OrderFulfilledEvent;
import org.mediamarktsaturn.order.order.eventsource.event.OrderPaidEvent;
import org.mediamarktsaturn.order.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static akka.actor.ActorRef.noSender;
import static org.mediamarktsaturn.order.order.dto.OrderStatus.FULFILLMENT;
import static org.mediamarktsaturn.order.order.dto.OrderStatus.FULFILLMENT_FAILED;
import static org.mediamarktsaturn.order.order.eventsource.event.OrderFulfilledEvent.FulFilamentStatus.FAILED;
import static org.mediamarktsaturn.order.order.eventsource.event.OrderFulfilledEvent.FulFilamentStatus.SUCCESS;

@Slf4j
@Component("orderManagerPersistenceActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderManagerPersistenceActor extends EventSourcedBehavior<OrderCommand, OrderEvent, OrderManagerPersistenceActor.OrderState> {

    @Autowired
    private OrderService orderService;

    private OrderState orderState = new OrderState();
    private ActorContext<OrderCommand> context;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public final class OrderState {
        public Long orderId;
        public OrderStatus orderStatus;
    }

    public static Behavior<OrderCommand> create(PersistenceId persistenceId) {
        return Behaviors.setup(context -> new OrderManagerPersistenceActor(persistenceId));
    }

    public OrderManagerPersistenceActor() {
        this(PersistenceId.of("OrderManagerPersistenceActor","order-manager-persistent-actor-id"));
    }

    private OrderManagerPersistenceActor(PersistenceId persistenceId) {
        super(persistenceId, SupervisorStrategy.restartWithBackoff(Duration.ofSeconds(10), Duration.ofSeconds(30), 0.2));
    }

    private EffectBuilder<OrderEvent, OrderState> handleUpdateOrderStatusCommand(UpdateOrderStatusCommand updateCommand) {
        OrderEvent event = null;
        switch (updateCommand.getNewStatus()) {
            case PAID:
                event = new OrderPaidEvent(updateCommand.getOrderId());
                break;
            case CLOSED:
                event = new OrderClosedEvent(updateCommand.getOrderId());
                break;
            case FULFILLMENT:
                event = new OrderFulfilledEvent(updateCommand.getOrderId(), SUCCESS);
                break;
            case FULFILLMENT_FAILED:
                event = new OrderFulfilledEvent(updateCommand.getOrderId(), FAILED);
                break;
        }
        if (event == null){
            updateCommand.replyTo.tell(StatusReply.error("Not expected order status " + updateCommand.getNewStatus()), noSender());
            return Effect().none();
        }
        OrderDetailsDto orderDetailsDto = orderService.updateOrderStatus(updateCommand.getOrderId(), updateCommand.getNewStatus());
        return Effect().persist(event)
                .thenRun(orderState -> updateCommand.replyTo.tell(StatusReply.success(orderDetailsDto), noSender()));
    }

    private Effect<OrderEvent, OrderState> handleRetrieveOrderCommand(RetrieveOrderCommand retrieveOrderCommand) {
        OrderDetailsDto order = orderService.getOrder(retrieveOrderCommand.getOrderId());
        if (order == null) {
            retrieveOrderCommand.replyTo.tell(StatusReply.error("There is no order with id " + retrieveOrderCommand.getOrderId()), noSender());
        } else {
            retrieveOrderCommand.replyTo.tell(StatusReply.success(order), noSender());
        }
        return Effect().none();
    }


    private Effect<OrderEvent, OrderState> handleCreateOrderCommand(CreateOrderCommand createOrderCommand) {
        OrderDetailsDto order = orderService.createOrder(createOrderCommand.getOrder());
        return Effect().persist(new OrderCreatedEvent(order.getId()))
                .thenRun(orderState -> createOrderCommand.getReplyTo().tell(StatusReply.success(order), noSender()));
    }


    @Override
    public OrderState emptyState() {
        return new OrderState();
    }

    @Override
    public CommandHandler<OrderCommand, OrderEvent, OrderState> commandHandler() {
        newCommandHandlerBuilder().forAnyState()
                .onCommand(CreateOrderCommand.class, this::handleCreateOrderCommand)
                .onCommand(RetrieveOrderCommand.class, this::handleRetrieveOrderCommand)
                .onCommand(UpdateOrderStatusCommand.class, this::handleUpdateOrderStatusCommand)
                .build();
        return null;
    }

    @Override
    public EventHandler<OrderState, OrderEvent> eventHandler() {
        return newEventHandlerBuilder().forAnyState()
                .onEvent(OrderCreatedEvent.class, this::handleOrderCreatedEvent)
                .onEvent(OrderPaidEvent.class, this::handleOrderPaidEvent)
                .onEvent(OrderFulfilledEvent.class, this::handleOrderFulfilledEvent)

        .build();
    }

    private OrderState handleOrderCreatedEvent(OrderState state, OrderCreatedEvent event) {
        state.setOrderId(event.getOrderId());
        state.setOrderStatus(OrderStatus.CREATED);
        return state;
    }

    private OrderState handleOrderPaidEvent(OrderState state, OrderPaidEvent event) {
        state.setOrderId(event.getOrderId());
        state.setOrderStatus(OrderStatus.PAID);
        return state;
    }

    private OrderState handleOrderFulfilledEvent(OrderState state, OrderFulfilledEvent event) {
        state.setOrderId(event.getOrderId());
        state.setOrderStatus(event.getFulFilamentStatus() == SUCCESS ? FULFILLMENT : FULFILLMENT_FAILED);
        return state;
    }

//    private  void handleGetOrderShippingInfoCommand(GetOrderShippingInfoCommand command) {
//        sender().tell(Optional.ofNullable(loadOrder(command.orderId))
//                .map(OrderDetailsDto::getShippingInfo), getSelf());
//    }
//
//    private void handleGetOrderPaymentInfoCommand(GetOrderPaymentInfoCommand command) {
//        sender().tell(Optional.ofNullable(loadOrder(command.orderId))
//                .map(OrderDetailsDto::getPaymentInfo), getSelf());
//    }
}
