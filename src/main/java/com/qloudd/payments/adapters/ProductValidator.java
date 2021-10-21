package com.qloudd.payments.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qloudd.payments.commons.Function;
import com.qloudd.payments.entity.Product;
import com.qloudd.payments.enums.CommandCode;
import com.qloudd.payments.exceptions.ValidationException;
import com.qloudd.payments.model.command.Command;
import com.qloudd.payments.model.integration.MauvePaymentGatewayConfig;
import com.qloudd.payments.repository.AccountRepository;
import com.qloudd.payments.repository.AccountTypeRepository;
import com.qloudd.payments.repository.ProductRepository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ProductValidator extends BasicValidator<Product> {

    public ProductValidator(ProductRepository productRepository, AccountRepository accountRepository) {
        super(productRepository, accountRepository, null);
    }

    @Override
    public Validator<Product> validate(Product product, Function function) throws ValidationException {
        List<String> errors = new ArrayList<>();
        requireValidName(product.getName());
        List<String> rangeConfigErrors = requireValidRangeConfigurations(product.getConfiguration().getCharges());
        errors.addAll(rangeConfigErrors);
        List<String> productCommandErrors = requireValidProductCommands(product.getConfiguration().getCommands());
        errors.addAll(productCommandErrors);
        if(!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return this;
    }

    void requireValidName(String name) throws ValidationException {
        List<String> errors = new ArrayList<>();
        int maxChars = 50;
        if (!StringUtils.hasText(name)) {
            errors.add("Invalid name");
        } else {
            if (name.length() > maxChars) {
                errors.add("Name must be less tha " + maxChars + " chars");
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }



    private List<String> requireValidProductCommands(List<Command> commands) {
        List<String> errorList = new ArrayList<>();
        commands.parallelStream().forEach((command) -> {
            // command must have name set
            if (!StringUtils.hasText(command.getCode())) {
                errorList.add("Field [code] is required");
            } else {
                // command is present -> It Must exist
                CommandCode commandCode = null;
                try {
                    commandCode = CommandCode.resolve(command.getCode());
                } catch (NoSuchElementException e) {
                    errorList.add(e.getMessage());
                }

                // Validate Service Specific fields
                if (commandCode != null) {
                    if (commandCode.equals(CommandCode.MPESA_STK_PUSH)) {
                        // apiKey must be present
                        MauvePaymentGatewayConfig mauvePaymentGatewayConfig = null;
                        try {
                            mauvePaymentGatewayConfig = new ObjectMapper()
                                    .convertValue(command.getConfiguration(), MauvePaymentGatewayConfig.class);
                            // Api key is required
                            if (!StringUtils.hasText(mauvePaymentGatewayConfig.getApiKey())) {
                                errorList.add("Field [ apiKey ] must be set for Mauve Payment Configuration");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorList.add("Invalid command configuration object");
                        }

                    }
                }
            }
        });
        return errorList;
    }
}
