package org.mediamarktsaturn.order.common.eventsource.command;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface OrderCommand extends Serializable {
    default String getPersistenceKey() {
        return Thread.currentThread().getId() + ":" + getClass().getSimpleName();
    }

    boolean isPersisted();
    void setPersisted(boolean persisted);
}
