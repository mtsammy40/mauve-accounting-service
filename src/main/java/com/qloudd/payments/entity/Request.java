package com.qloudd.payments.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table(name = "requests", schema = "accounting")
@Entity
@Data
public class Request {
    @Id
    @Column(name = "trx_id")
    private UUID trxId;

    @Lob
    @Column(name = "trx_type")
    private String trxType;

    @Lob
    @Column(name = "recipient")
    private String recipient;

    @Lob
    @Column(name = "sender")
    private String sender;

    @Column(name = "amount", precision = 131089)
    private BigDecimal amount;

    @Lob
    @Column(name = "status")
    private String status;

    @Column(name = "initiated_at")
    private Instant initiatedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType")
    @Column(name = "details")
    private JsonNode details;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "initiator")
    private UUID initiator;

    @Lob
    @Column(name = "stage")
    private String stage;

}