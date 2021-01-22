package org.mediamarktsaturn.order.order.util;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.format.DateTimeFormatter;

public class IsoLocalDateTimeDeserializer extends LocalDateTimeDeserializer {
    public IsoLocalDateTimeDeserializer() {
        super(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
