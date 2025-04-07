package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BillingServiceGrpcClient {

    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.port:9001}") int serverPort
    ){
        log.info("Connecting to Billing Service GRPC service at {}:{}",serverAddress,serverPort);
        ManagedChannel channel= ManagedChannelBuilder.forAddress(serverAddress,serverPort).usePlaintext().build();
        blockingStub=BillingServiceGrpc.newBlockingStub(channel);
    }
    public BillingResponse createBillingAccount(String id,String email,String name){
        BillingRequest billingRequest= BillingRequest.newBuilder()
                .setEmail(email)
                .setName(name)
                .setPatientId(id)
                .build();
        BillingResponse billingResponse=blockingStub.createBillingAccount(billingRequest);
        log.info("Recived Response from Billing service grpc {}",billingResponse);
        return billingResponse;
    }
}
