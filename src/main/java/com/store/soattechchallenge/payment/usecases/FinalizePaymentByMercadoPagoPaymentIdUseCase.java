package com.store.soattechchallenge.payment.usecases;

import com.store.soattechchallenge.payment.domain.PaymentStatus;
import com.store.soattechchallenge.payment.domain.entities.Payment;
import com.store.soattechchallenge.payment.gateways.MercadoPagoClientGateway;
import com.store.soattechchallenge.payment.gateways.PaymentRepositoryGateway;
import com.store.soattechchallenge.payment.gateways.exceptions.EntityNotFoundException;
import com.store.soattechchallenge.payment.usecases.commands.FinalizePaymentByMercadoPagoPaymentIdCommand;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.model.MPOrderStatus;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.exception.MPClientException;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.model.MPOrder;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.model.MPPayment;
import com.store.soattechchallenge.utils.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class FinalizePaymentByMercadoPagoPaymentIdUseCase {
    private final PaymentRepositoryGateway paymentRepositoryGateway;
    private final MercadoPagoClientGateway mercadoPagoClientGateway;

    public FinalizePaymentByMercadoPagoPaymentIdUseCase(
            PaymentRepositoryGateway paymentRepositoryGateway,
            MercadoPagoClientGateway mercadoPagoClientGateway
    ) {
        this.paymentRepositoryGateway = paymentRepositoryGateway;
        this.mercadoPagoClientGateway = mercadoPagoClientGateway;
    }

    public Payment execute(FinalizePaymentByMercadoPagoPaymentIdCommand command) {
        try {
            MPPayment mpPayment = this.mercadoPagoClientGateway.findPaymentById(command.paymentId());
            MPOrder mpOrder = this.mercadoPagoClientGateway.findOrderById(mpPayment.order().id());
            String id = mpOrder.externalReference();
            String externalId = Long.toString(mpOrder.id());
            if (paymentRepositoryGateway.existsByExternalId(externalId)) {
                throw new CustomException(
                        "Payment with external ID " + externalId + " already exists",
                        HttpStatus.BAD_REQUEST,
                        String.valueOf(HttpStatus.BAD_REQUEST.value()),
                        LocalDateTime.now(),
                        UUID.randomUUID()
                );
            }

            Payment payment = this.paymentRepositoryGateway.findById(id);
            payment.setExternalId(externalId);
            payment.finalize(this.convertPaymentStatusToDomain(mpOrder.status()));
            return this.paymentRepositoryGateway.save(payment);
        } catch (EntityNotFoundException error) {
            throw new CustomException(
                    "Payment not found",
                    HttpStatus.NOT_FOUND,
                    String.valueOf(HttpStatus.NOT_FOUND.value()),
                    LocalDateTime.now(),
                    UUID.randomUUID()
            );
        } catch (IllegalArgumentException error) {
            throw new CustomException(
                    error.getMessage(),
                    HttpStatus.BAD_REQUEST,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    LocalDateTime.now(),
                    UUID.randomUUID()
            );
        } catch (MPClientException error) {
            throw new CustomException(
                    "Error interacting with the payment gateway: " + error.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    LocalDateTime.now(),
                    UUID.randomUUID()
            );
        }
    }

    private PaymentStatus convertPaymentStatusToDomain(MPOrderStatus mpOrderStatus) {
        return switch (mpOrderStatus) {
            case opened -> PaymentStatus.OPENED;
            case closed -> PaymentStatus.CLOSED;
            case expired -> PaymentStatus.EXPIRED;
        };
    }
}
