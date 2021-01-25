package org.mediamarktsaturn.order.order.service;

import org.junit.jupiter.api.Test;
import org.mediamarktsaturn.order.common.dto.OrderDetailsDto;
import org.mediamarktsaturn.order.common.exception.ProcessingOrderException;
import org.mediamarktsaturn.order.order.BaseIntegrationTest;
import org.mediamarktsaturn.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mediamarktsaturn.order.common.dto.OrderStatus.CLOSED;
import static org.mediamarktsaturn.order.common.dto.OrderStatus.PAID;

public class OrderManagerServiceTest extends BaseIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void testShouldCreateNewOrder(){
        OrderDetailsDto createdOrder = orderService.createOrder(createTestOrderDto());
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(orderRepository.findById(createdOrder.getId())).isPresent();
    }

    @Test
    public void testShouldThroughExceptionWhenUpdatingNotExistingOrder(){
        assertThatThrownBy(()-> orderService.updateOrderStatus(-300l, PAID))
                .isInstanceOf(ProcessingOrderException.class);
    }

    @Test
    public void testShouldUpdateOrder(){
        OrderDetailsDto createdOrder = orderService.createOrder(createTestOrderDto());
        OrderDetailsDto updateOrder = orderService.updateOrderStatus(createdOrder.getId(), CLOSED);
        assertThat(updateOrder).isNotNull();
        assertThat(orderRepository.findById(createdOrder.getId()).get().getStatus()).isEqualTo(CLOSED);
    }
}
