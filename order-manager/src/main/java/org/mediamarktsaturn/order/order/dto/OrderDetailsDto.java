package org.mediamarktsaturn.order.order.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mediamarktsaturn.order.order.util.IsoLocalDateTimeDeserializer;
import org.mediamarktsaturn.order.order.util.IsoLocalDateTimeSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDto implements Serializable {
    private Long id;
    private Long createdByUserId;
    @JsonDeserialize(using = IsoLocalDateTimeDeserializer.class)
    @JsonSerialize(using = IsoLocalDateTimeSerializer.class)
    private LocalDateTime orderTime = LocalDateTime.now();
    private OrderStatus status = OrderStatus.RECEIVED;
    private ShippingInfoDto shippingInfo;
    private PaymentInfoDto paymentInfo;
    private List<OrderItemDto> orderItems = new ArrayList<>();
}
