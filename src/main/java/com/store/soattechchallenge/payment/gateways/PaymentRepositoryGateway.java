package com.store.soattechchallenge.payment.gateways;

import com.store.soattechchallenge.payment.domain.entities.Payment;

public interface PaymentRepositoryGateway {
    public Payment findById (String id);
    public Boolean existsById(String id);
    public Boolean existsByExternalId(String id);
    public Payment save (Payment payment);
}
