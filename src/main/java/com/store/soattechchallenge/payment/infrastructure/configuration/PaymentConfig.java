package com.store.soattechchallenge.payment.infrastructure.configuration;

import com.google.zxing.qrcode.QRCodeWriter;
import com.store.soattechchallenge.payment.infrastructure.api.validator.MercadoPagoWebhookValidator;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.MercadoPagoClient;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.impl.MercadoPagoClientImpl;
import com.store.soattechchallenge.payment.infrastructure.mappers.PaymentMapper;
import com.store.soattechchallenge.payment.infrastructure.mappers.PaymentProductMapper;
import com.store.soattechchallenge.payment.infrastructure.mappers.PaymentStatusMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {
    @Bean
    PaymentMapper paymentMapper () {
        return new PaymentMapper();
    }

    @Bean
    PaymentStatusMapper paymentStatusMapper () {
        return new PaymentStatusMapper();
    }

    @Bean
    PaymentProductMapper paymentProductMapper () {
        return new PaymentProductMapper();
    }

    @Bean
    QRCodeWriter qrCodeWriter () {
        return new QRCodeWriter();
    }

    @Bean
    MercadoPagoIntegrationConfig mercadoPagoIntegrationConfig () {
        return new MercadoPagoIntegrationConfig();
    }

    @Bean
    MercadoPagoClient mercadoPagoClient (MercadoPagoIntegrationConfig mercadoPagoIntegrationConfig) {
        return new MercadoPagoClientImpl(mercadoPagoIntegrationConfig);
    }

    @Bean
    MercadoPagoWebhookValidator mercadoPagoWebhookValidator (
            MercadoPagoIntegrationConfig mercadoPagoIntegrationConfig
    ) {
        return new MercadoPagoWebhookValidator(mercadoPagoIntegrationConfig);
    }
}
