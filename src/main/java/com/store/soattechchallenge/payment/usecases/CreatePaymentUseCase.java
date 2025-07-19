package com.store.soattechchallenge.payment.usecases;

import com.store.soattechchallenge.payment.gateways.PaymentGateway;
import com.store.soattechchallenge.payment.domain.entities.Payment;
import com.store.soattechchallenge.payment.domain.PaymentStatus;
import com.store.soattechchallenge.payment.gateways.PaymentRepositoryGateway;
import com.store.soattechchallenge.payment.usecases.commands.CreatePaymentCommand;
import com.store.soattechchallenge.payment.gateways.exceptions.PaymentCreationException;
import com.store.soattechchallenge.utils.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreatePaymentUseCase {
    private final PaymentRepositoryGateway paymentRepositoryGateway;
    private final PaymentGateway paymentGateway;

    public CreatePaymentUseCase(
            PaymentRepositoryGateway paymentRepositoryGateway,
            PaymentGateway paymentGateway
    ) {
        this.paymentRepositoryGateway = paymentRepositoryGateway;
        this.paymentGateway = paymentGateway;
    }

    public Payment execute(CreatePaymentCommand command) {
        if (this.paymentRepositoryGateway.existsById(command.id())) {
            throw new CustomException(
                    "Payment with ID " + command.id() + " already exists",
                    HttpStatus.BAD_REQUEST,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    LocalDateTime.now(),
                    UUID.randomUUID()
            );
        }

        LocalDateTime expiration = LocalDateTime.now().plusMinutes(10);
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(
                command.id(),
                "empty-" + command.id(),
                PaymentStatus.OPENED,
                command.vlTotalPedido(),
                "",
                expiration,
                now,
                now
        );

        try {
            payment = this.paymentGateway.create(payment, command.products());
        } catch (PaymentCreationException error) {
            throw new CustomException(
                    error.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    LocalDateTime.now(),
                    UUID.randomUUID()
            );
        }
        return this.paymentRepositoryGateway.save(payment);
    }
}
