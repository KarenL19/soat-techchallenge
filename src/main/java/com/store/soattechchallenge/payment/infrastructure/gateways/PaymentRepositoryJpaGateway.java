package com.store.soattechchallenge.payment.infrastructure.gateways;

import com.store.soattechchallenge.payment.domain.entities.Payment;
import com.store.soattechchallenge.payment.gateways.PaymentRepositoryGateway;
import com.store.soattechchallenge.payment.gateways.exceptions.EntityNotFoundException;
import com.store.soattechchallenge.payment.infrastructure.mappers.PaymentMapper;
import com.store.soattechchallenge.payment.infrastructure.jpa.JpaPayment;
import com.store.soattechchallenge.payment.infrastructure.jpa.PaymentJpaRepository;

import java.util.Optional;

public class PaymentRepositoryJpaGateway implements PaymentRepositoryGateway {
    private final PaymentJpaRepository jpaRepository;
    private final PaymentMapper paymentMapper;

    public PaymentRepositoryJpaGateway(
            PaymentJpaRepository paymentJpaRepository,
            PaymentMapper paymentMapper
    ) {
        this.jpaRepository = paymentJpaRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Payment findById(String id) {
        Optional<JpaPayment> optionalJpaPayment = this.jpaRepository.findById(id);
        if (optionalJpaPayment.isEmpty()) {
            throw new EntityNotFoundException("Payment with ID " + id + " not found");
        }

        return this.paymentMapper.fromJpaToDomain(optionalJpaPayment.get());
    }

    @Override
    public Boolean existsById(String id) {
        return this.jpaRepository.existsById(id);
    }

    @Override
    public Boolean existsByExternalId(String id) {
        return this.jpaRepository.existsByExternalId(id);
    }

    @Override
    public Payment save(Payment payment) {
        JpaPayment jpaPayment = this.jpaRepository.save(
                this.paymentMapper.fromDomainToJPA(payment)
        );

        return this.paymentMapper.fromJpaToDomain(jpaPayment);
    }
}
