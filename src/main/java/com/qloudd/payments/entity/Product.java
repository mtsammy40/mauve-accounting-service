package com.qloudd.payments.entity;

import com.qloudd.payments.commons.converters.JsonToMapConverter;
import com.qloudd.payments.entity.converters.ProductConfigurationConverter;
import com.qloudd.payments.entity.converters.ProductStatusConverter;
import com.qloudd.payments.enums.ProductStatus;
import com.qloudd.payments.model.ProductConfiguration;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "TransactionType")
public class Product {
    private Long id;
    private String name;
    private ProductStatus status;
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

    @Type(type = "json")
    @Column(name = "configurations", columnDefinition = "json")
    @Convert(attributeName = "configurations", converter = ProductConfigurationConverter.class)
    public ProductConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ProductConfiguration chargeConfiguration) {
        this.configuration = chargeConfiguration;
    }

    @Convert(attributeName = "configurations", converter = ProductStatusConverter.class)
    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus productStatus) {
        this.status = productStatus;
    }
}
