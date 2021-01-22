package org.mediamarktsaturn.order.order.eventsource.command;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mediamarktsaturn.order.order.dto.OrderDetailsDto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class CreateOrderCommand implements OrderCommand {
    private final OrderDetailsDto order;
    private final ActorRef replyTo;
    @Setter
    private boolean isPersisted = false;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class CreateOrderResponse implements Serializable {
        private Long orderId;
        private String redirectURL;
    }

    @Override
    public String getPersistenceKey() {
        return OrderCommand.super.getPersistenceKey() + ":"
                + Optional.ofNullable(order.getId()).map(Objects::toString).orElse(order.getOrderTime().toString())
                ;
    }
}
