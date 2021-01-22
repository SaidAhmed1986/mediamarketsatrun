package org.mediamarktsaturn.order.order.messaging;

import lombok.RequiredArgsConstructor;
import org.mediamarktsaturn.order.order.eventsource.event.OrderEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventsPublisher {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${spring.kafka.producer.topic}")
    private String orderEventsTopic;

    public void publishEvent(OrderEvent orderEvent) {
        kafkaTemplate.send(orderEventsTopic, orderEvent.getClass().getSimpleName() + "-" + orderEvent.getOrderId(), orderEvent);
    }
}
