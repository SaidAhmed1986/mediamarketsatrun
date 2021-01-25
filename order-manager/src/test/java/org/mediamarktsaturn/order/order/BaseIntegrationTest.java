package org.mediamarktsaturn.order.order;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mediamarktsaturn.order.common.dto.OrderDetailsDto;
import org.mediamarktsaturn.order.common.dto.OrderItemDto;
import org.mediamarktsaturn.order.common.dto.OrderStatus;
import org.mediamarktsaturn.order.common.dto.PaymentInfoDto;
import org.mediamarktsaturn.order.common.dto.ShippingInfoDto;
import org.mediamarktsaturn.order.common.eventsource.event.OrderEvent;
import org.mediamarktsaturn.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class BaseIntegrationTest {
    @Value("${spring.kafka.producer.topic}")
    protected String topic;

    @Autowired
    protected EmbeddedKafkaBroker embeddedKafkaBroker;

    BlockingQueue<ConsumerRecord<String, OrderEvent>> consumedRecords;

    KafkaMessageListenerContainer<String, OrderEvent> container;

    @Autowired
    protected OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
        DefaultKafkaConsumerFactory<String, OrderEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new JsonDeserializer<>(OrderEvent.class));
        ContainerProperties containerProperties = new ContainerProperties("embedded-test-topic");
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        consumedRecords = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, OrderEvent>) consumedRecords::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterEach
    void tearDown() {
        container.stop();
    }

    protected OrderDetailsDto createTestOrderDto(){
        OrderDetailsDto order = new OrderDetailsDto();
        order.setCreatedByUserId(1l);
        order.setPaymentInfo(new PaymentInfoDto(null, "CARD", 30.0, 5.0, "code"));
        order.setShippingInfo(new ShippingInfoDto(null, "address", "postCode", "city", "phone"));
        order.setOrderItems(Arrays.asList(new OrderItemDto(null, 1l, 2, 15)));
        order.setStatus(OrderStatus.CREATED);
        order.setOrderTime(LocalDateTime.now());
        return order;
    }
}
