package com.store.soattechchallenge.payment.controller;

import com.google.zxing.qrcode.QRCodeWriter;
import com.store.soattechchallenge.payment.gateways.MercadoPagoClientGateway;
import com.store.soattechchallenge.payment.gateways.PaymentGateway;
import com.store.soattechchallenge.payment.gateways.PaymentRepositoryGateway;
import com.store.soattechchallenge.payment.gateways.QrCodeWriterGateway;
import com.store.soattechchallenge.payment.infrastructure.gateways.MercadoPagoAPIClientGateway;
import com.store.soattechchallenge.payment.infrastructure.gateways.QrCodeWriterZxingGateway;
import com.store.soattechchallenge.payment.usecases.CreatePaymentUseCase;
import com.store.soattechchallenge.payment.usecases.FinalizePaymentByMercadoPagoPaymentIdUseCase;
import com.store.soattechchallenge.payment.usecases.FindPaymentByIdUseCase;
import com.store.soattechchallenge.payment.usecases.RenderQrCodeUseCase;
import com.store.soattechchallenge.payment.usecases.commands.CreatePaymentCommand;
import com.store.soattechchallenge.payment.usecases.commands.FinalizePaymentByMercadoPagoPaymentIdCommand;
import com.store.soattechchallenge.payment.usecases.commands.FindPaymentByIdCommand;
import com.store.soattechchallenge.payment.usecases.commands.RenderQrCodeCommand;
import com.store.soattechchallenge.payment.domain.entities.Payment;
import com.store.soattechchallenge.payment.infrastructure.api.dto.PaymentResponseDTO;
import com.store.soattechchallenge.payment.infrastructure.configuration.MercadoPagoIntegrationConfig;
import com.store.soattechchallenge.payment.infrastructure.gateways.MercadoPagoPaymentGateway;
import com.store.soattechchallenge.payment.infrastructure.gateways.PaymentRepositoryJpaGateway;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.MercadoPagoClient;
import com.store.soattechchallenge.payment.infrastructure.jpa.PaymentJpaRepository;
import com.store.soattechchallenge.payment.infrastructure.mappers.PaymentMapper;
import com.store.soattechchallenge.payment.presenters.PaymentHttpPresenter;
import com.store.soattechchallenge.payment.presenters.QrCodePresenter;

import java.awt.image.BufferedImage;

public class PaymentController {
    private final PaymentMapper paymentMapper;
    private final QrCodeWriterGateway qrCodeWriterGateway;
    private final PaymentRepositoryGateway paymentRepositoryGateway;
    private final MercadoPagoClientGateway mercadoPagoClientGateway;
    private final PaymentGateway paymentGateway;

    public PaymentController(
            PaymentJpaRepository paymentJpaRepository,
            PaymentMapper paymentMapper,
            QRCodeWriter qrCodeWriter,
            MercadoPagoIntegrationConfig mercadoPagoIntegrationConfig,
            MercadoPagoClient mercadoPagoClient
    ) {
        this.paymentMapper = paymentMapper;
        this.paymentGateway = new MercadoPagoPaymentGateway(mercadoPagoIntegrationConfig, mercadoPagoClient);
        this.paymentRepositoryGateway = new PaymentRepositoryJpaGateway(paymentJpaRepository, this.paymentMapper);
        this.qrCodeWriterGateway = new QrCodeWriterZxingGateway(qrCodeWriter);
        this.mercadoPagoClientGateway = new MercadoPagoAPIClientGateway(mercadoPagoClient);
    }

    public PaymentResponseDTO findById(FindPaymentByIdCommand command) {
        FindPaymentByIdUseCase useCase = new FindPaymentByIdUseCase(this.paymentRepositoryGateway);
        PaymentHttpPresenter presenter = new PaymentHttpPresenter(this.paymentMapper);
        Payment payment = useCase.execute(command);
        return presenter.present(payment);
    }

    public BufferedImage renderQrCode(RenderQrCodeCommand command) {
        RenderQrCodeUseCase useCase = new RenderQrCodeUseCase(this.paymentRepositoryGateway, this.qrCodeWriterGateway);
        QrCodePresenter presenter = new QrCodePresenter();
        BufferedImage qrCode = useCase.execute(command);
        return presenter.present(qrCode);
    }

    public PaymentResponseDTO create(CreatePaymentCommand command) {
        CreatePaymentUseCase useCase = new CreatePaymentUseCase(this.paymentRepositoryGateway, this.paymentGateway);
        PaymentHttpPresenter presenter = new PaymentHttpPresenter(this.paymentMapper);
        Payment payment = useCase.execute(command);
        return presenter.present(payment);
    }

    public PaymentResponseDTO finalizePaymentByMercadoPago(FinalizePaymentByMercadoPagoPaymentIdCommand command) {
        FinalizePaymentByMercadoPagoPaymentIdUseCase useCase = new FinalizePaymentByMercadoPagoPaymentIdUseCase(
                this.paymentRepositoryGateway, this.mercadoPagoClientGateway
        );

        PaymentHttpPresenter presenter = new PaymentHttpPresenter(this.paymentMapper);
        Payment payment = useCase.execute(command);
        return presenter.present(payment);
    }
}
