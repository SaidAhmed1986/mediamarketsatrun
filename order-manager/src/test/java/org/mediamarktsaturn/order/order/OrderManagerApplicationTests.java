package org.mediamarktsaturn.order.order;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mediamarktsaturn.order.common.dto.OrderDetailsDto;
import org.mediamarktsaturn.order.common.dto.OrderStatus;
import org.mediamarktsaturn.order.common.eventsource.command.CreateOrderCommand.CreateOrderResponse;
import org.mediamarktsaturn.order.common.eventsource.event.OrderCreatedEvent;
import org.mediamarktsaturn.order.common.eventsource.event.OrderPaidEvent;
import org.mediamarktsaturn.order.common.eventsource.event.OrderStatusChangedEvent;
import org.mediamarktsaturn.order.rest.OrderManagerServer;
import org.mediamarktsaturn.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static akka.http.javadsl.marshallers.jackson.Jackson.unmarshaller;
import static akka.http.javadsl.model.ContentTypes.APPLICATION_JSON;
import static akka.http.javadsl.model.HttpRequest.PUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest
@TestInstance(PER_CLASS)
class OrderManagerApplicationTests extends BaseIntegrationTest {

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private OrderManagerServer server;

    private Http httpClient ;

    @Autowired
    private OrderService orderService;

    private String orderEndPoint;

    @BeforeAll
    public void init(){
        server.startHttpServer();
        httpClient = Http.get(actorSystem);
        orderEndPoint = "http://localhost:8888/order-service/order";
    }

    @Test
    public void testShouldGenerateOrderCreatedEventWhenCreateOrderEndPointCalled() throws Exception {
        CreateOrderResponse createOrderResponse =
                httpClient.singleRequest(HttpRequest.POST(orderEndPoint)
                        .withEntity(HttpEntities.create(APPLICATION_JSON, getCreateOrderRequestJson())))
                        .thenCompose(response -> {
                            assertThat(response.status().isSuccess()).as(response.status().defaultMessage()).isTrue();
                            return unmarshaller(CreateOrderResponse.class).unmarshal(response.entity(), actorSystem);
                        }).toCompletableFuture().join();

        assertThat(createOrderResponse).isNotNull();
        assertThat(createOrderResponse.getOrderId()).isNotNull();
        assertThat(createOrderResponse.getRedirectURL()).isNotNull();

        Thread.sleep(500); // wait until actor processing is complete
        assertThat(consumedRecords.isEmpty()).isFalse();
        assertThat(consumedRecords.poll().value()).isInstanceOfSatisfying(OrderCreatedEvent.class,
                event-> assertThat(event.getOrderId()).isEqualTo(createOrderResponse.getOrderId()));
        consumedRecords.clear();
    }

    @Test
    public void testShouldGenerateOrderPaidEventWhenOrderStatusChangedToPaid() throws Exception {
        OrderDetailsDto createdOrder = orderService.createOrder(createTestOrderDto());
        String updateUrl = String.format("%s/%s?status=%s", orderEndPoint, createdOrder.getId(), OrderStatus.PAID);
        httpClient.singleRequest(PUT(updateUrl))
                .thenAccept(response -> {
                    assertThat(response.status().isSuccess()).as(response.status().defaultMessage()).isTrue();
                }).toCompletableFuture().join();
        Thread.sleep(500); // wait until actor processing is complete
        assertThat(consumedRecords.isEmpty()).isFalse();
        assertThat(consumedRecords.size()).isEqualTo(2);// 2 events should be generated , order status change and order paid
        assertThat(consumedRecords.poll().value()).isInstanceOfSatisfying(OrderStatusChangedEvent.class,
                event-> assertThat(event.getOrderId()).isEqualTo(createdOrder.getId()));
        assertThat(consumedRecords.poll().value()).isInstanceOfSatisfying(OrderPaidEvent.class,
                event-> assertThat(event.getOrderId()).isEqualTo(createdOrder.getId()));
    }

    private String getCreateOrderRequestJson(){
        return "{\n" +
                "    \"createdByUserId\": 1,\n" +
                "    \"orderItems\": [\n" +
                "        {\n" +
                "            \"quantity\": 2,\n" +
                "            \"skuId\": 1,\n" +
                "            \"unitPrice\": 15\n" +
                "        }\n" +
                "    ],\n" +
                "    \"orderTime\": \"2021-01-21T17:49:06\",\n" +
                "    \"paymentInfo\": {\n" +
                "        \"discountAmount\": 0,\n" +
                "        \"paymentAmount\": 30,\n" +
                "        \"paymentMethod\": \"CARD\",\n" +
                "        \"voucherCode\": null\n" +
                "    },\n" +
                "    \"shippingInfo\": {\n" +
                "        \"address\": \"address\",\n" +
                "        \"city\": \"Munich\",\n" +
                "        \"contactPhone\": \"01234567\",\n" +
                "        \"postCode\": \"80992\"\n" +
                "    },\n" +
                "    \"status\": \"CREATED\"\n" +
                "}";
    }

}
