package com.qloudd.payments.network.service;

import com.qloudd.payments.config.GrpcConfig;
import com.qloudd.payments.entity.Account;
import com.qloudd.payments.exceptions.AccountCreationException;
import com.qloudd.payments.proto.AccountCreationRequest;
import com.qloudd.payments.proto.AccountCreationResponse;
import com.qloudd.payments.proto.AccountServiceGrpc;
import com.qloudd.payments.service.AccountService;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AccountsServiceImpl extends AccountServiceGrpc.AccountServiceImplBase {
    Logger LOG = LoggerFactory.getLogger(GrpcConfig.class);

    @Autowired
    private AccountService accountService;

    public AccountsServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void create(AccountCreationRequest request, StreamObserver<AccountCreationResponse> responseObserver) {
        try {
            Account account = accountService.create(Account.from(request));
            AccountCreationResponse response = AccountCreationResponse
                    .newBuilder()
                    .setResponseCode(String.valueOf(HttpStatus.OK.value()))
                    .setAccountNumber(account.getAccountNumber())
                    .setBalance(account.getBalance().toPlainString())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (AccountCreationException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("Free");
            responseObserver.onError(e);
        }
    }
}
