package com.store.soattechchallenge.payment.infrastructure.gateways;

import com.store.soattechchallenge.payment.gateways.PaymentGateway;
import com.store.soattechchallenge.payment.gateways.PaymentProductDTO;
import com.store.soattechchallenge.payment.infrastructure.configuration.MercadoPagoIntegrationConfig;
import com.store.soattechchallenge.payment.gateways.exceptions.PaymentCreationException;
import com.store.soattechchallenge.payment.domain.entities.Payment;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.MercadoPagoClient;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.exception.MPClientException;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.model.MPCreateOrderRequest;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.model.MPCreateOrderResponse;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.model.MPItem;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MercadoPagoPaymentGateway implements PaymentGateway {
    private final String USER_ID;
    private final String POS;
    private final String CALLBACK_URL;
    private final MercadoPagoClient mercadoPagoClient;

    public MercadoPagoPaymentGateway(
            MercadoPagoIntegrationConfig mercadoPagoIntegrationConfig,
            MercadoPagoClient mercadoPagoClient
    ) {
        this.USER_ID = mercadoPagoIntegrationConfig.getUserId();
        this.POS = mercadoPagoIntegrationConfig.getPos();
        this.CALLBACK_URL = mercadoPagoIntegrationConfig.getCallbackUrl() + "?apikey=" +
                mercadoPagoIntegrationConfig.getWebhookToken();

        this.mercadoPagoClient = mercadoPagoClient;
    }

    @Override
    public Payment create(Payment payment, List<PaymentProductDTO> products) {
        List<MPItem> MPItems = products.stream()
                .map(product -> new MPItem(
                        product.name(),
                        product.category(),
                        product.quantity(),
                        "unit",
                        product.unitPrice(),
                        product.getTotalValue()
                ))
                .collect(Collectors.toList());

        ZoneOffset offset = OffsetDateTime.now().getOffset();
        OffsetDateTime offsetDateTime = payment.getExpiration().atOffset(offset);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String description = "Order #" + payment.getId();
        MPCreateOrderRequest mpCreateOrderRequest = new MPCreateOrderRequest(
                payment.getId(),
                payment.getTotalOrderValue(),
                description,
                description,
                offsetDateTime.format(formatter),
                MPItems,
                this.CALLBACK_URL
        );

        try {
            MPCreateOrderResponse MPCreateOrderResponse = this.mercadoPagoClient.createDynamicQROrder(
                    this.USER_ID, this.POS, mpCreateOrderRequest
            );

            payment.setQrCode(MPCreateOrderResponse.qrData());
            return payment;
        } catch (MPClientException error) {
            throw new PaymentCreationException("Error creating payment: " + error.getMessage());
        }
    }
}
