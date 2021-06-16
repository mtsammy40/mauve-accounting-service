package com.qloudd.payments;

import com.qloudd.payments.config.GrpcConfig;
import com.qloudd.payments.network.service.AccountsServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@SpringBootApplication
@EnableTransactionManagement
public class QlouddPaymentsApplication {

    Logger LOG = LoggerFactory.getLogger(QlouddPaymentsApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(QlouddPaymentsApplication.class, args);

//        startGrpcServer();
    }


}
