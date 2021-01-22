package org.mediamarktsaturn.order.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Table
@Getter
@Setter
@Entity
@AllArgsConstructor
public class MailBoxMessage {
    @Id
    @GeneratedValue
    private Long id;

    private String messageKey;

    @Lob
    @Column(name = "message", columnDefinition="BLOB")
    private byte[] message;
    private LocalDateTime arrivedAt;

    public MailBoxMessage(String messageKey, byte[] message) {
        this.messageKey = messageKey;
        this.message = message;
        arrivedAt = LocalDateTime.now();
    }
}
