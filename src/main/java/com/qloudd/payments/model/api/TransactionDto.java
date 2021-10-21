package com.qloudd.payments.model.api;

import com.qloudd.payments.entity.Account;
import com.qloudd.payments.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class TransactionDto {

    @NotNull(message = "Source must be defined")
    private Identifier source;

    @NotNull(message = "Destination must be defined")
    private Identifier destination;

    @NotNull(message = "Amount must be specified")
    @Min(value = 0, message = "Amount cannot be less than 0")
    private BigDecimal amount;

    @NotNull(message = "ThirdPartyReference must be 10 - 12 characters")
    @Size(min = 18, max = 36, message = "ThirdPartyReference must be 18 - 36 characters")
    private String thirdPartyReference;

    @NotNull(message = "Product Id must be set")
    private Long productId;

    @NotNull(message = "Initiator must be set")
    private UUID initiator;

    private String transactionDesc;

    Account sourceAccount;
    Account destAccount;
}
