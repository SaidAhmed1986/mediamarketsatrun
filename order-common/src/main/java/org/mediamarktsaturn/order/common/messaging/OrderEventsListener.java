package org.mediamarktsaturn.order.common.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mediamarktsaturn.order.common.eventsource.IOrderEventHandler;
import org.mediamarktsaturn.order.common.eventsource.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = "#{'${spring.kafka.consumer.topic}'.split(',')}",
        groupId = "#{'${spring.kafka.consumer.group-id}'}",
        containerFactory = "kafkaListenerContainerFactory")
public class OrderEventsListener {

    private final IOrderEventHandler eventsHandler;

    @KafkaHandler
    public void onMessage(@Payload OrderEvent event) {
        log.info("Received event of type {} for order {} and will be handled by {}",
                event.getClass().getSimpleName(), event.getOrderId(), eventsHandler.getClass().getSimpleName());
        eventsHandler.handleEvent(event);
    }
}
