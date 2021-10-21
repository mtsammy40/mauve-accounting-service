package com.qloudd.payments.entity;

import com.qloudd.payments.entity.converters.StatusConverter;
import com.qloudd.payments.model.AccountTypeConfiguration;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Table
@TypeDef(name = "json", typeClass = JsonStringType.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountType {
    public enum Status {
        ACTIVE, SUSPENDED, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Convert(attributeName = "status", converter = StatusConverter.class)
    private String status;

    @Type(type = "json")
    @Column(name = "configurations", columnDefinition = "json")
    private AccountTypeConfiguration configurations;
}
