package org.mediamarktsaturn.order.order.actor;

import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.mediamarktsaturn.order.order.BaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

@Ignore
public class OrderManagerActorTest extends BaseIntegrationTest {

    @Autowired
    private ActorSystem actorSystem;

    @AfterAll
    public void tearDown() {
        Duration duration = Duration.create(10L, TimeUnit.SECONDS);
        TestKit.shutdownActorSystem(actorSystem, duration, true);
        actorSystem = null;
    }

}
