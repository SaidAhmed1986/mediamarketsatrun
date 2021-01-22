package org.mediamarktsaturn.order.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mediamarktsaturn.order.order.dto.OrderDetailsDto;
import org.mediamarktsaturn.order.order.dto.OrderItemDto;
import org.mediamarktsaturn.order.order.dto.OrderStatus;
import org.mediamarktsaturn.order.order.dto.PaymentInfoDto;
import org.mediamarktsaturn.order.order.dto.ShippingInfoDto;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_details")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private Long createdByUserId;

    private LocalDateTime orderTime;

    private OrderStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_info_id", referencedColumnName = "id")
    private ShippingInfo shippingInfo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_info_id", referencedColumnName = "id")
    private PaymentInfo paymentInfo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;

    public Order(OrderDetailsDto dto) {
        fromDto(dto);
    }

    @Override
    public <T> T toDto(Class<T> dtoClass) {
        OrderDetailsDto orderDetailsDto = (OrderDetailsDto) super.toDto(dtoClass);
        orderDetailsDto.setPaymentInfo(paymentInfo.toDto(PaymentInfoDto.class));
        orderDetailsDto.setShippingInfo(shippingInfo.toDto(ShippingInfoDto.class));
        if (orderItems != null){
            for (OrderItem orderItem : orderItems) {
                orderDetailsDto.getOrderItems().add(orderItem.toDto(OrderItemDto.class));
            }
        }
        return (T) orderDetailsDto;
    }

    @Override
    public <T> void fromDto(T dto) {
        super.fromDto(dto);
        OrderDetailsDto orderDetailsDto = (OrderDetailsDto) dto;
        paymentInfo = new PaymentInfo(orderDetailsDto.getPaymentInfo());
        shippingInfo = new ShippingInfo(orderDetailsDto.getShippingInfo());
        orderItems = new ArrayList<>();
        for (OrderItemDto itemDto : orderDetailsDto.getOrderItems()){
            orderItems.add(new OrderItem(itemDto));
        }
    }
}
