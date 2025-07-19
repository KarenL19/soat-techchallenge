package com.store.soattechchallenge.payment.gateways;

import com.store.soattechchallenge.payment.domain.entities.Payment;

import java.util.List;

public interface PaymentGateway {
    public Payment create(Payment payment, List<PaymentProductDTO> products);
}
