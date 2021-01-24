package org.mediamarktsaturn.order.repository.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;


@Slf4j
public abstract class BaseEntity {
    public <T> T toDto(Class<T> dtoClass) {
        try {
            T target = BeanUtils.instantiateClass(dtoClass);
            BeanUtils.copyProperties(this, target);
            return target;
        } catch (Exception e) {
            log.error("Failed to convert bean", e);
        }
        return null;
    }

    public <T> void fromDto(T dto) {
        try {
            BeanUtils.copyProperties(dto, this);
        } catch (Exception e) {
            log.error("Failed to convert bean", e);
        }
    }
}
