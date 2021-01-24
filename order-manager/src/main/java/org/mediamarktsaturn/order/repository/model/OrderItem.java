package org.mediamarktsaturn.order.repository.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mediamarktsaturn.order.common.dto.OrderItemDto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long skuId;//catalog item id
    private int quantity;
    private double unitPrice;

    public OrderItem(final OrderItemDto dto) {
        this.fromDto(dto);
    }
}
