package com.fdmgroup.paymentService.service;

import com.fdmgroup.paymentService.entity.TransactionDetails;
import com.fdmgroup.paymentService.model.PaymentMode;
import com.fdmgroup.paymentService.model.PaymentRequest;
import com.fdmgroup.paymentService.model.PaymentResponse;
import com.fdmgroup.paymentService.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;


    @Override
    public long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording Payment Details: {}:", paymentRequest);

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .paymentStatus("SUCCESS")
                .amount(paymentRequest.getAmount())
                .build();

        transactionDetailsRepository.save(transactionDetails);

        log.info("Transaction completed with Id: {}", transactionDetails.getId());
        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(long orderId) {
        TransactionDetails transactionDetails = transactionDetailsRepository.findByOrderId(orderId);
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(transactionDetails.getId())
                .paymentDate(transactionDetails.getPaymentDate())
                .orderId(transactionDetails.getOrderId())
                .amount(transactionDetails.getAmount())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .status(transactionDetails.getPaymentStatus())
                .build();
        return paymentResponse;
    }
}
