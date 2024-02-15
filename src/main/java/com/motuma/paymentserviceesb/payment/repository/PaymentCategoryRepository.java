package com.motuma.paymentserviceesb.payment.repository;

import com.motuma.paymentserviceesb.payment.model.PaymentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentCategoryRepository extends JpaRepository<PaymentCategory,Long> {
    Boolean existsByPaymentCategoryCodeIgnoreCase(String paymentCategory);
    Boolean existsByPaymentSourceNameIgnoreCase(String paymentSourceName);
}
