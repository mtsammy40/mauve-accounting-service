package com.qloudd.payments.entity;

import com.qloudd.payments.entity.converters.StatusConverter;
import com.qloudd.payments.enums.Status;
import com.qloudd.payments.model.ProductConfiguration;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.time.LocalDateTime;

@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Entity
@Table(name = "Product")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Builder
public class Product {
    @NonNull
    private Long id;
    private String name;
    private Status status;
    private ProductConfiguration configuration;
    private LocalDateTime createdAt;

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

    @Enumerated(value = EnumType.STRING)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @CreationTimestamp
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
