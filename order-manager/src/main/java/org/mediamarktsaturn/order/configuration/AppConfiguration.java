package org.mediamarktsaturn.order.configuration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.mediamarktsaturn.order.common.configuration.CommonAppConfiguration;
import org.mediamarktsaturn.order.common.configuration.SpringExtension;
import org.mediamarktsaturn.order.common.eventsource.event.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@ComponentScan
public class AppConfiguration {
    @Bean
    public ActorRef orderManagerActorRef(ActorSystem actorSystem, SpringExtension springExtension){
        return actorSystem.actorOf(springExtension.props("orderManagerActor")
                .withDispatcher("order-manager-dispatcher"), "order-manager-actor");
    }
}
