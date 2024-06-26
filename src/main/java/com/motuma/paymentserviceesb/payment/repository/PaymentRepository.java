package com.motuma.paymentserviceesb.payment.repository;

import com.motuma.paymentserviceesb.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Boolean existsByOrderId(String orderId);
    Boolean existsByReferenceIdOrInvoiceIdIgnoreCase(String referenceId,String invoiceId);
}
