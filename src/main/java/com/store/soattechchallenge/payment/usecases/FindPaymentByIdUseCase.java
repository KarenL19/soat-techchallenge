package com.store.soattechchallenge.payment.usecases;

import com.store.soattechchallenge.payment.gateways.PaymentRepositoryGateway;
import com.store.soattechchallenge.payment.usecases.commands.FindPaymentByIdCommand;
import com.store.soattechchallenge.payment.domain.entities.Payment;
import com.store.soattechchallenge.payment.gateways.exceptions.EntityNotFoundException;
import com.store.soattechchallenge.utils.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class FindPaymentByIdUseCase {
    private final PaymentRepositoryGateway paymentRepositoryGateway;

    public FindPaymentByIdUseCase(PaymentRepositoryGateway paymentRepositoryGateway) {
        this.paymentRepositoryGateway = paymentRepositoryGateway;
    }

    public Payment execute(FindPaymentByIdCommand command) {
        try {
            return this.paymentRepositoryGateway.findById(command.id());
        } catch (EntityNotFoundException error) {
            throw new CustomException(
                    "Payment not found",
                    HttpStatus.NOT_FOUND,
                    String.valueOf(HttpStatus.NOT_FOUND.value()),
                    LocalDateTime.now(),
                    UUID.randomUUID()
            );
        }
    }
}
