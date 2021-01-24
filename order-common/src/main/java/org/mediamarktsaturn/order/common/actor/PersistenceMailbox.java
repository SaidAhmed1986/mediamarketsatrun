package org.mediamarktsaturn.order.common.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.Envelope;
import akka.dispatch.MailboxType;
import akka.dispatch.MessageQueue;
import akka.dispatch.ProducesMessageQueue;
import akka.dispatch.UnboundedDequeBasedMailbox;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.mediamarktsaturn.order.common.eventsource.command.OrderCommand;
import org.mediamarktsaturn.order.common.repository.MailBoxMessageRepository;
import org.mediamarktsaturn.order.common.repository.model.MailBoxMessage;
import scala.Option;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.CompletableFuture;

import static org.mediamarktsaturn.order.common.util.SpringContext.getBean;

@Slf4j
public class PersistenceMailbox implements MailboxType, ProducesMessageQueue<PersistenceMailbox.OrderManagerMessageQueue> {
    public PersistenceMailbox(ActorSystem.Settings settings, Config config) {

    }

    @Override
    public MessageQueue create(Option<ActorRef> owner, Option<ActorSystem> system) {
        return new OrderManagerMessageQueue();
    }

    public static class OrderManagerMessageQueue extends UnboundedDequeBasedMailbox.MessageQueue {
        @Override
        public void enqueue(ActorRef receiver, Envelope handle) {
            saveMessage(handle.message());
            super.enqueue(receiver, handle);
        }

        @Override
        public Envelope dequeue() {
            Envelope envelope = super.dequeue();
            if (envelope != null)
                removeMessage(envelope.message());
            return envelope;
        }

        @Override
        public void cleanUp(ActorRef owner, MessageQueue deadLetters) {
            super.cleanUp(owner, deadLetters);
            CompletableFuture.runAsync(()-> getBean(MailBoxMessageRepository.class).deleteAll());
        }

        @Override
        public void clear() {
            super.clear();
            CompletableFuture.runAsync(()-> getBean(MailBoxMessageRepository.class).deleteAll());
        }

        private void saveMessage(Object message) {
            CompletableFuture.runAsync(() -> {
                if (message instanceof OrderCommand) {
                    OrderCommand command = (OrderCommand) message;
                    if (command.isPersisted()) return;

                    try {
                        getBean(MailBoxMessageRepository.class).save(new MailBoxMessage(command.getPersistenceKey(), toBytes(command)));
                    } catch (Exception e) {
                        log.warn("Failed to persist order command {}, {}", command.getPersistenceKey(), e);
                    }
                }
            });
        }

        private void removeMessage(Object message) {
            CompletableFuture.runAsync(() -> {
                if (message instanceof OrderCommand) {
                    OrderCommand command = (OrderCommand) message;
                    if (!command.isPersisted()) return;
                    try {
                        MailBoxMessageRepository messageRepository = getBean(MailBoxMessageRepository.class);
                        messageRepository.findALLByMessageKeyOrderByArrivedAtAsc(command.getPersistenceKey())
                                .stream()
                                .limit(1)
                                .forEach(messageRepository::delete);
                    } catch (Exception e) {
                        log.warn("Failed to persist order command {}, {}", command.getPersistenceKey(), e);
                    }
                }
            });
        }


        private byte[] toBytes(OrderCommand message) throws IOException {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
                return byteArrayOutputStream.toByteArray();
            }
        }
    }
}
