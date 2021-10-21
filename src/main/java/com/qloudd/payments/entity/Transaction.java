package com.qloudd.payments.entity;

import com.qloudd.payments.enums.CommandCode;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.model.TransactionMisc;
import com.qloudd.payments.model.api.TransactionDto;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "Transaction")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Transaction {

    public enum Status {
        NEW, INITIATED, COMPLETED_OK, COMPLETED_FAILED, PROCESSING, UNKNOWN
    }

    public enum ProcessingStage {
        ACCOUNTING, EXECUTING, REVERSING_ACCOUNTING, REVERSING_EXECUTION, COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JoinColumn(referencedColumnName = "id")
    @ManyToOne
    private Account sourceAccount;
    @JoinColumn(referencedColumnName = "id")
    @ManyToOne
    private Account destAccount;
    private String destIdentifier;
    private BigDecimal amount;
    @ManyToOne()
    @JoinColumn(name = "product")
    private Product product;
    @Enumerated(value = EnumType.STRING)
    private Status status;
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    private Calendar initiated;
    private Calendar completed;
    @Type(type = "json")
    private TransactionMisc misc;
    private String thirdPartyReference;
    private UUID initiator;
    @Enumerated(value = EnumType.STRING)
    private ProcessingStage processingStage;

    @Transient
    private BigDecimal totalAmount;

    @Transient
    boolean reverseTransaction;

    public TransactionMisc getMisc() {
        if (misc == null) {
            return new TransactionMisc();
        } else {
            return misc;
        }
    }

    public void complete() {
        this.setCompleted(Calendar.getInstance());
        this.setProcessingStage(ProcessingStage.COMPLETED);
        this.status = Status.COMPLETED_OK;
    }

    public void fail(String reason) {
        reason = reason == null ? "FAILED" : reason;
        this.setCompleted(Calendar.getInstance());
        this.setStatus(Status.COMPLETED_FAILED);
        this.getMisc().setDescription(reason);
    }
}
