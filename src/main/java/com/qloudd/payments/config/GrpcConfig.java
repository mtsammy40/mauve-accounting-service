package com.qloudd.payments.config;

import com.qloudd.payments.network.service.AccountsServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.IOException;

@Configuration
public class GrpcConfig {
    Logger LOG = LoggerFactory.getLogger(GrpcConfig.class);

    private final AccountsServiceImpl accountService;

    @Autowired
    public GrpcConfig(AccountsServiceImpl accountService) {
        this.accountService = accountService;
    }

    private static final int port = 8087;

    @EventListener(ApplicationReadyEvent.class)
    public void startGrpcServer() {
        LOG.info("Starting GRPC Server on port :: {}", port);
        Server server = ServerBuilder
                .forPort(port)
                .addService(accountService).build();
        try {
            server.start();
            server.awaitTermination();
            LOG.info("GRPC Server is listening on port :: {}", port);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
