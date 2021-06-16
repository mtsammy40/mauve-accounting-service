package com.qloudd.payments.entity;

import com.qloudd.payments.commons.Function;
import com.qloudd.payments.commons.converters.JsonToMapConverter;
import com.qloudd.payments.model.AccountTypeConfiguration;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@TypeDef(
        name = "json",
        typeClass = JsonStringType.class
)
public class AccountType {
    private Long id;
    private String name;
    private String status;
    private AccountTypeConfiguration configurations;

    public AccountType() {
    }

    public AccountType(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Type(type = "json")
    @Column(name = "configurations", columnDefinition = "json")
    @Convert(attributeName = "configurations", converter = JsonToMapConverter.class)
    public AccountTypeConfiguration getConfigurations() {
        return configurations;
    }

    public void setConfigurations(AccountTypeConfiguration configurations) {
        this.configurations = configurations;
    }


}
