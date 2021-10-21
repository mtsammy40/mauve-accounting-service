package com.qloudd.payments.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Table(name = "configuration")
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Configuration implements Serializable {
    @Id
    @Column(name = "config_id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Type(type = "jsonb")
    @Column(name = "config", columnDefinition = "jsonb", nullable = false)
    private ConfigData config;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "type", nullable = false)
    private String type;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ConfigData {
        private MpesaConfiguration mpesa;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MpesaConfiguration {
        private String passkey;
        private String shortCode;
        private String consumerKey;
        private String lnmShortCode;
        private String initiatorName;
        private String consumerSecret;
        private String securityCredential;
    }
}
