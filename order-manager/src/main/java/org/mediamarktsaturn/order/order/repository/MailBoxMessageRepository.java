package org.mediamarktsaturn.order.order.repository;

import org.mediamarktsaturn.order.order.model.MailBoxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailBoxMessageRepository extends JpaRepository<MailBoxMessage, Long> {
    List<MailBoxMessage> findALLByMessageKeyOrderByArrivedAtAsc(String key);
}
