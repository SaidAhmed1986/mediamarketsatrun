package org.mediamarktsaturn.order.common.actor;

import akka.actor.ActorRef;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mediamarktsaturn.order.common.eventsource.command.OrderCommand;
import org.mediamarktsaturn.order.common.repository.MailBoxMessageRepository;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailBoxLoader {

    private final MailBoxMessageRepository repository;
    private final ActorRef mailBoxActor;


    /**
     * Load saved commands on startup and send it again to order manager actor for processing
     * @param event application start event
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("MailBox message loader started to load saved messages");
         repository.findAll().forEach(mailBoxMessage -> {
             try {
                 OrderCommand command = fromBytes(mailBoxMessage.getMessage());
                 command.setPersisted(true);
                 mailBoxActor.tell(command, ActorRef.noSender());
             } catch (Exception e) {
                 log.error("Failed to load saved command with id {}, {}", mailBoxMessage.getMessageKey(), e);
             }
         });
    }

    private OrderCommand fromBytes(byte[] data) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (OrderCommand) objectInputStream.readObject();
        }
    }
}
