package org.mediamarktsaturn.order.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto implements Serializable {
    private Long id;
    private Long skuId;//catalog item id
    private int quantity;
    private double unitPrice;
}