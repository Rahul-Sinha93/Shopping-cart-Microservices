package com.fdmgroup.paymentService.repository;

import com.fdmgroup.paymentService.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails,Long> {

    TransactionDetails findByOrderId(long orderId);
}
