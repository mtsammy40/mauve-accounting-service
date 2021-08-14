package com.qloudd.payments.entity;

import com.qloudd.payments.entity.converters.ProductConfigurationConverter;
import com.qloudd.payments.entity.converters.StatusConverter;
import com.qloudd.payments.enums.Status;
import com.qloudd.payments.model.ProductConfiguration;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Entity
@Table(name = "TransactionType")
public class Product {
    private Long id;
    private String name;
    private Status status;
    private ProductConfiguration configuration;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Type(type = "jsonb")
    @Column(name = "configurations", columnDefinition = "jsonb")
    public ProductConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ProductConfiguration chargeConfiguration) {
        this.configuration = chargeConfiguration;
    }

    @Convert(attributeName = "status", converter = StatusConverter.class)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
