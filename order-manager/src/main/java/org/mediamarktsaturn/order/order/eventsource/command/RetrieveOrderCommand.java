package org.mediamarktsaturn.order.order.eventsource.command;

import akka.actor.ActorRef;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public final class RetrieveOrderCommand implements OrderCommand {
    private final Long orderId;
    public final ActorRef replyTo;
    @Setter
    private boolean isPersisted = false;

    @Override
    public String getPersistenceKey() {
        return OrderCommand.super.getPersistenceKey() + ":" + orderId;
    }
}