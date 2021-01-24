package org.mediamarktsaturn.order.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mediamarktsaturn.order.common.dto.OrderDetailsDto;
import org.mediamarktsaturn.order.common.dto.OrderStatus;
import org.mediamarktsaturn.order.repository.OrderRepository;
import org.mediamarktsaturn.order.repository.model.Order;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private LoadingCache<Long, OrderDetailsDto> ORDER_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build(new CacheLoader<>() {
                @Override
                public OrderDetailsDto load(Long id) {
                    return loadOrder(id).orElse(null);
                }
            });

    public OrderDetailsDto createOrder(OrderDetailsDto orderDetailsDto) {
        Order order = new Order(orderDetailsDto);
        order.setStatus(OrderStatus.CREATED);
        return orderRepository.save(order).toDto(OrderDetailsDto.class);
    }

     Optional<OrderDetailsDto> loadOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> Optional.ofNullable(order.toDto(OrderDetailsDto.class)))
                .orElse(Optional.empty());
    }

    public OrderDetailsDto getOrder(Long orderId) {
        OrderDetailsDto orderDetails = null;
        try {
            orderDetails = ORDER_CACHE.get(orderId);
        } catch (Exception e) {
            log.error("Failed to load order with id {}, {}", orderId, e.getMessage());
        }
        return orderDetails;
    }

    public OrderDetailsDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Optional<Order> existingOrderOp = orderRepository.findById(orderId);
        if (existingOrderOp.isEmpty()) {
            throw new org.mediamarktsaturn.order.common.exception.ProcessingOrderException("Order with id " + orderId + " does not exist", 404);
        }
        Order existingOrder = existingOrderOp.get();
        existingOrder.setStatus(newStatus);
        return orderRepository.save(existingOrder).toDto(OrderDetailsDto.class);
    }
}
