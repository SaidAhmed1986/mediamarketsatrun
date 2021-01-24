package org.mediamarktsaturn.order.repository.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mediamarktsaturn.order.common.dto.ShippingInfoDto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shipping_info")
public class ShippingInfo extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String address;
    private String postCode;
    private String city;
    private String contactPhone;

    public ShippingInfo(ShippingInfoDto dto){
        fromDto(dto);
    }
}
