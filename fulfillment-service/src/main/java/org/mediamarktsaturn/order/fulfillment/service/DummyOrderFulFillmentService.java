package org.mediamarktsaturn.order.fulfillment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mediamarktsaturn.order.common.dto.OrderDetailsDto;
import org.mediamarktsaturn.order.common.dto.OrderStatus;
import org.mediamarktsaturn.order.fulfillment.rest.RestClient;
import org.springframework.stereotype.Service;

import java.util.Random;

import static org.mediamarktsaturn.order.common.dto.OrderStatus.FULFILLMENT;
import static org.mediamarktsaturn.order.common.dto.OrderStatus.FULFILLMENT_FAILED;

@Log4j2
@Service
@RequiredArgsConstructor
public class DummyOrderFulFillmentService implements IOrderFulfillmentService {

    public static final int MIN_FULFILLMENT_PROCESSION_TIME = 500;
    public static final int MAX_FULFILLMENT_PROCESSION_TIME = 1000;
    private final RestClient restClient;

    @Override
    public void fulFillOrder(final Long orderId) {
        restClient.getOrderDetails(orderId)
                .thenApplyAsync(this::doFulFilament)
                .thenAcceptAsync(orderStatus -> restClient.updateOrderStatus(orderId, orderStatus))
                .toCompletableFuture().join();

    }

    private OrderStatus doFulFilament(OrderDetailsDto orderDetailsDto) {

        //simulate order fulfillment time by waiting for random time
        try {
            Thread.sleep(new Random().nextInt(MAX_FULFILLMENT_PROCESSION_TIME - MIN_FULFILLMENT_PROCESSION_TIME) + MIN_FULFILLMENT_PROCESSION_TIME);
        } catch (InterruptedException e) {}

        String postCode = orderDetailsDto.getShippingInfo().getPostCode();
        return postCode.equalsIgnoreCase("dummy") ? FULFILLMENT_FAILED : FULFILLMENT;
    }

}
