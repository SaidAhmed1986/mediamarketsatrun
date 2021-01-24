package org.mediamarktsaturn.order.fulfillment.rest;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import lombok.extern.log4j.Log4j2;
import org.mediamarktsaturn.order.common.dto.OrderDetailsDto;
import org.mediamarktsaturn.order.common.dto.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.marshallers.jackson.Jackson.unmarshaller;
import static akka.http.javadsl.model.HttpRequest.GET;
import static akka.http.javadsl.model.HttpRequest.PUT;


@Log4j2
@Component
public class RestClient {

    @Autowired
    private ActorSystem actorSystem;

    @Value("${orderServiceURL}")
    private String orderServiceURL;

    private Http httpClient;

    @PostConstruct
     void init() {
        httpClient = Http.get(actorSystem);
    }


    public CompletionStage<OrderDetailsDto> getOrderDetails(Long orderId) {
        return httpClient.singleRequest(GET(orderServiceURL.concat("/").concat(orderId.toString())))
                .thenCompose(response -> unmarshaller(OrderDetailsDto.class).unmarshal(response.entity(), actorSystem));
    }

    public CompletionStage<OrderDetailsDto> updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        String updateUrl = String.format("%s/%s?status=%s", orderServiceURL, orderId, orderStatus);
        return httpClient.singleRequest(PUT(updateUrl))
                .thenCompose(response -> {
                    if (response.status().isSuccess()) {
                        log.info("Order fulfillment status updated to {} for order {}", orderStatus, orderId);
                        return unmarshaller(OrderDetailsDto.class).unmarshal(response.entity(), actorSystem);
                    } else {
                        log.error("Failed to change fulfillment status for order {}, error from server {}",
                                orderId, response.status().reason());
                        return null;
                    }
                });
    }
}
