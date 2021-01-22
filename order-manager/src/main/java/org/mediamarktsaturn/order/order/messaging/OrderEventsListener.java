package org.mediamarktsaturn.order.order.messaging;

import lombok.RequiredArgsConstructor;
import org.mediamarktsaturn.order.order.eventsource.OrderEventsHandler;
import org.mediamarktsaturn.order.order.eventsource.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = "#{'${spring.kafka.consumer.topic}'.split(',')}",
        groupId = "#{'${spring.kafka.consumer.group-id}'}",
        containerFactory = "kafkaListenerContainerFactory")
public class OrderEventsListener {

    private final OrderEventsHandler eventsHandler;

    @KafkaHandler
    public void onMessage(@Payload OrderEvent event) {
        eventsHandler.handleEvent(event);
    }
}
