package com.pm.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceGrpc.BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);



    @Override
    public void createBillingAccount(BillingRequest billingRequest, StreamObserver<billing.BillingResponse> billingResponseStreamObserver) {

        log.info("Billing account is created.");
        BillingResponse billingResponse= BillingResponse.newBuilder()
                .setAccountId("1")
                .setStatus("Active")
                .build();

        billingResponseStreamObserver.onNext(billingResponse);
        billingResponseStreamObserver.onCompleted();
    }
}
