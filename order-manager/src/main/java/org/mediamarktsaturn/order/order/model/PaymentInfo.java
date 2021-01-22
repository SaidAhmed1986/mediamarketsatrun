package org.mediamarktsaturn.order.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mediamarktsaturn.order.order.dto.PaymentInfoDto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_info")
public class PaymentInfo extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String paymentMethod;
    private Double paymentAmount;
    private Double discountAmount;
    private String voucherCode;

    public PaymentInfo(PaymentInfoDto dto) {
        fromDto(dto);
    }
}
