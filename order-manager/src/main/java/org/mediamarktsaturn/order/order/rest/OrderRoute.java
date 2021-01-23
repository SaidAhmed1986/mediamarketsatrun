package org.mediamarktsaturn.order.order.rest;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.RejectionHandler;
import akka.http.javadsl.server.Route;
import akka.pattern.PatternsCS;
import lombok.RequiredArgsConstructor;
import org.mediamarktsaturn.order.order.dto.OrderDetailsDto;
import org.mediamarktsaturn.order.order.dto.OrderStatus;
import org.mediamarktsaturn.order.order.dto.PaymentInfoDto;
import org.mediamarktsaturn.order.order.dto.ShippingInfoDto;
import org.mediamarktsaturn.order.order.eventsource.command.CreateOrderCommand;
import org.mediamarktsaturn.order.order.eventsource.command.CreateOrderCommand.CreateOrderResponse;
import org.mediamarktsaturn.order.order.eventsource.command.GetOrderPaymentInfoCommand;
import org.mediamarktsaturn.order.order.eventsource.command.GetOrderShippingInfoCommand;
import org.mediamarktsaturn.order.order.eventsource.command.RetrieveOrderCommand;
import org.mediamarktsaturn.order.order.eventsource.command.UpdateOrderStatusCommand;
import org.mediamarktsaturn.order.order.exception.OrderManagerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.complete;
import static akka.http.javadsl.server.Directives.concat;
import static akka.http.javadsl.server.Directives.entity;
import static akka.http.javadsl.server.Directives.get;
import static akka.http.javadsl.server.Directives.onSuccess;
import static akka.http.javadsl.server.Directives.parameter;
import static akka.http.javadsl.server.Directives.path;
import static akka.http.javadsl.server.Directives.pathPrefix;
import static akka.http.javadsl.server.Directives.post;
import static akka.http.javadsl.server.Directives.put;
import static akka.http.javadsl.server.Directives.route;
import static akka.http.javadsl.server.PathMatchers.longSegment;
import static akka.http.javadsl.server.PathMatchers.segment;
import static akka.http.scaladsl.model.StatusCode.int2StatusCode;


@Component
@RequiredArgsConstructor
public class OrderRoute {

    private final ActorSystem actorSystem;
    private final ActorRef orderManagerActorRef;
    private Duration timeout;

    @Value("${orderManager.payment.redirectURL}")
    private String paymentRedirectURL;

    @PostConstruct
    public void init() {
        timeout = actorSystem.settings().config().getDuration("akka.routes.ask-timeout");
    }

    public Route getRoute() {
        return pathPrefix("order-service",
                () -> path("order", this::createOrder)
                        .orElse(path(segment("order").slash(longSegment()), id -> route(concat(getOrderDetails(id), updateOrderStatus(id)))))
                        .orElse(path(segment("order").slash(longSegment()).slash().concat("payment-info"),
                                id -> route(getOrderPaymentInfo(id))
                        ))
                        .orElse(path(segment("order").slash(longSegment()).slash().concat("shipping-info"),
                                id -> route(getOrderShippingInfo(id))
                        ))
        ).seal(RejectionHandler.defaultHandler(), this.createErrorResponse());
    }

    private Route getOrderPaymentInfo(Long orderId) {
        return get(() -> {
            CompletionStage<Optional<PaymentInfoDto>> paymentInfo = PatternsCS.ask(orderManagerActorRef, new GetOrderPaymentInfoCommand(orderId), timeout)
                    .thenApply(obj -> (Optional<PaymentInfoDto>) obj);

            return onSuccess(() -> paymentInfo, performed -> {
                if (performed.isPresent())
                    return complete(StatusCodes.OK, performed.get(), Jackson.marshaller());
                else
                    return complete(StatusCodes.NOT_FOUND, "Order with Id " + orderId + " does not exist");
            });
        });
    }

    private Route getOrderShippingInfo(Long orderId) {
        return get(() -> {
            CompletionStage<Optional<ShippingInfoDto>> shippingInfo = PatternsCS.ask(orderManagerActorRef, new GetOrderShippingInfoCommand(orderId), timeout)
                    .thenApply(obj -> (Optional<ShippingInfoDto>) obj);

            return onSuccess(() -> shippingInfo, performed -> {
                if (performed.isPresent())
                    return complete(StatusCodes.OK, performed.get(), Jackson.marshaller());
                else
                    return complete(StatusCodes.NOT_FOUND, "Order with Id " + orderId + " does not exist");
            });
        });
    }

    private Route getOrderDetails(Long id) {
        return get(() -> {
            CompletionStage<Optional<OrderDetailsDto>> order = PatternsCS.ask(orderManagerActorRef, new RetrieveOrderCommand(id), timeout)
                    .thenApply(obj -> (Optional<OrderDetailsDto>) obj);

            return onSuccess(() -> order, performed -> {
                if (performed.isPresent())
                    return complete(StatusCodes.OK, performed.get(), Jackson.marshaller());
                else
                    return complete(StatusCodes.NOT_FOUND, "Order with Id " + id + " does not exist");
            });
        });
    }

    private Route updateOrderStatus(Long orderId) {
        return route(put(() -> parameter("status", orderStatusStr -> {
            Optional<OrderStatus> orderStatus = OrderStatus.fromString(orderStatusStr);
            if (orderStatus.isEmpty()) {
                return complete(StatusCodes.BAD_REQUEST, "Invalid order status");
            }
            CompletionStage<Optional<OrderDetailsDto>> updatedOrder = PatternsCS.ask(orderManagerActorRef,
                    new UpdateOrderStatusCommand(orderId, orderStatus.get()), timeout
            )
                    .thenApply(obj -> (Optional<OrderDetailsDto>) obj);
            return onSuccess(() -> updatedOrder, performed -> complete(StatusCodes.ACCEPTED, performed.get(), Jackson.marshaller()));
        })));
    }

    private Route createOrder() {
        return route(post(() -> entity(Jackson.unmarshaller(OrderDetailsDto.class), order -> {
            CompletionStage<OrderDetailsDto> orderCreated = PatternsCS.ask(orderManagerActorRef, new CreateOrderCommand(order), timeout)
                    .thenApply(obj -> (OrderDetailsDto) obj);
            return onSuccess(() -> orderCreated, performed -> complete(StatusCodes.CREATED,
                    new CreateOrderResponse(performed.getId(), paymentRedirectURL), Jackson.marshaller()
            ));
        })));
    }

    private ExceptionHandler createErrorResponse() {
        return ExceptionHandler.newBuilder()
                .match(OrderManagerException.class, exception ->
                        complete(int2StatusCode(exception.getStatusCode()), exception.getMessage()))
                .matchAny(ex -> complete(StatusCodes.BAD_REQUEST, ex.getMessage()))
                .build();
    }
}
