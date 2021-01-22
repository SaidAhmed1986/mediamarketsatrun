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
public class PaymentInfoDto implements Serializable {
    private Long id;
    private String paymentMethod;
    private Double paymentAmount;
    private Double discountAmount;
    private String voucherCode;
}