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
public class ShippingInfoDto implements Serializable {
    private Long id;
    private String address;
    private String postCode;
    private String city;
    private String contactPhone;
}