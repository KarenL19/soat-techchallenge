package com.store.soattechchallenge.payment.presenters;

import com.store.soattechchallenge.payment.domain.entities.Payment;
import com.store.soattechchallenge.payment.infrastructure.api.dto.PaymentResponseDTO;
import com.store.soattechchallenge.payment.infrastructure.mappers.PaymentMapper;

public class PaymentHttpPresenter {
    private final PaymentMapper paymentMapper;

    public PaymentHttpPresenter(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    public PaymentResponseDTO present(Payment payment) {
        return this.paymentMapper.fromDomainToDTO(payment);
    }
}
