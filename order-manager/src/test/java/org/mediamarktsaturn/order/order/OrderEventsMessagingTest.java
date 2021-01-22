package org.mediamarktsaturn.order.order;

import org.junit.jupiter.api.Test;
import org.mediamarktsaturn.order.order.messaging.OrderEventsPublisher;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderEventsMessagingTest extends BaseIntegrationTest{

    @Autowired
    OrderEventsPublisher eventsPublisher;

    @Test
    public void test() {

    }
}
