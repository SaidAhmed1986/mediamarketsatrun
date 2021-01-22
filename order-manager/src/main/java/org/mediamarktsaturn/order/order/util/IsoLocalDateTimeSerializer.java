package org.mediamarktsaturn.order.order.util;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.format.DateTimeFormatter;

public class IsoLocalDateTimeSerializer extends LocalDateTimeSerializer {
    public IsoLocalDateTimeSerializer() {
        super(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
