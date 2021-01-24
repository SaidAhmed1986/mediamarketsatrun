package org.mediamarktsaturn.order.fulfillment.configuration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.mediamarktsaturn.order.common.configuration.SpringExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
@ComponentScan
public class AppConfiguration {

    @Bean
    public ActorRef orderFulfillmentActorRef(ActorSystem actorSystem, SpringExtension springExtension){
        return actorSystem.actorOf(springExtension.props("orderFulfillmentActor")
                .withDispatcher("order-fulfillment-dispatcher"), "order-fulfillment-actor");
    }
}
