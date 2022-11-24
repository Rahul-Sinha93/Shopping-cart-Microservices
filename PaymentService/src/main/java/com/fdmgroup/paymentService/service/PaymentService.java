package com.fdmgroup.paymentService.service;

import com.fdmgroup.paymentService.model.PaymentRequest;
import com.fdmgroup.paymentService.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(long orderId);
}
