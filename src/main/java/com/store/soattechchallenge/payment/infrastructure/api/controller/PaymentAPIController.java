package com.store.soattechchallenge.payment.infrastructure.api.controller;

import com.google.zxing.qrcode.QRCodeWriter;
import com.store.soattechchallenge.payment.usecases.commands.FinalizePaymentByMercadoPagoPaymentIdCommand;
import com.store.soattechchallenge.payment.usecases.commands.CreatePaymentCommand;
import com.store.soattechchallenge.payment.usecases.commands.FindPaymentByIdCommand;
import com.store.soattechchallenge.payment.usecases.commands.RenderQrCodeCommand;
import com.store.soattechchallenge.payment.controller.PaymentController;
import com.store.soattechchallenge.payment.infrastructure.api.dto.PaymentResponseDTO;
import com.store.soattechchallenge.payment.infrastructure.api.dto.MercadoPagoWebhookDTO;
import com.store.soattechchallenge.payment.infrastructure.api.dto.PaymentCreateRequestDTO;
import com.store.soattechchallenge.payment.infrastructure.api.validator.MercadoPagoWebhookValidator;
import com.store.soattechchallenge.payment.infrastructure.configuration.MercadoPagoIntegrationConfig;
import com.store.soattechchallenge.payment.infrastructure.integrations.mercado_pago.MercadoPagoClient;
import com.store.soattechchallenge.payment.infrastructure.jpa.PaymentJpaRepository;
import com.store.soattechchallenge.payment.infrastructure.mappers.PaymentMapper;
import com.store.soattechchallenge.payment.infrastructure.mappers.PaymentProductMapper;
import com.store.soattechchallenge.payment.infrastructure.mappers.PaymentStatusMapper;
import com.store.soattechchallenge.utils.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment")
public class PaymentAPIController {
    private final PaymentController controller;
    private final PaymentProductMapper paymentProductMapper;
    private final MercadoPagoWebhookValidator mercadoPagoWebhookValidator;

    public PaymentAPIController(
            PaymentJpaRepository paymentJpaRepository,
            PaymentMapper paymentMapper,
            QRCodeWriter qrCodeWriter,
            MercadoPagoIntegrationConfig mercadoPagoIntegrationConfig,
            MercadoPagoClient mercadoPagoClient,
            PaymentProductMapper paymentProductMapper,
            PaymentStatusMapper paymentStatusMapper,
            MercadoPagoWebhookValidator mercadoPagoWebhookValidator
    ) {
        this.paymentProductMapper = paymentProductMapper;
        this.mercadoPagoWebhookValidator = mercadoPagoWebhookValidator;
        this.controller = new PaymentController(
                paymentJpaRepository,
                paymentMapper,
                qrCodeWriter,
                mercadoPagoIntegrationConfig,
                mercadoPagoClient,
                paymentStatusMapper
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> find(@PathVariable String id) {
        FindPaymentByIdCommand command = new FindPaymentByIdCommand(id);
        PaymentResponseDTO dto = this.controller.findById(command);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/qr")
    public void getQrCode(@PathVariable String id, HttpServletResponse response) {
        try {
            RenderQrCodeCommand command = new RenderQrCodeCommand(id, 600, 600);
            BufferedImage qrImage = this.controller.renderQrCode(command);
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            ImageIO.write(qrImage, "PNG", response.getOutputStream());
        } catch (IOException error) {
            throw new CustomException(
                    error.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    LocalDateTime.now(),
                    UUID.randomUUID()
            );
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PaymentResponseDTO> create(@Valid @RequestBody PaymentCreateRequestDTO paymentRequestDTO) {
        CreatePaymentCommand command = new CreatePaymentCommand(
                paymentRequestDTO.id(),
                paymentRequestDTO.totalOrderValue(),
                paymentRequestDTO.products()
                        .stream()
                        .map(this.paymentProductMapper::apiCreateDTOToGatewayDTO)
                        .collect(Collectors.toList())
        );

        PaymentResponseDTO dto = this.controller.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/notifications/mercado-pago")
    @Transactional
    public ResponseEntity<PaymentResponseDTO> mercadoPagoWebhook(
            HttpServletRequest request,
            @RequestBody MercadoPagoWebhookDTO mercadoPagoWebhookDTO
    ) {
        if (
                mercadoPagoWebhookDTO.action() == null ||
                        mercadoPagoWebhookDTO.type() == null ||
                        !mercadoPagoWebhookDTO.action().equals("payment.created") ||
                        !mercadoPagoWebhookDTO.type().equals("payment")
        ) {
            return ResponseEntity.noContent().build();
        }

        this.mercadoPagoWebhookValidator.validate(request);
        FinalizePaymentByMercadoPagoPaymentIdCommand command = new FinalizePaymentByMercadoPagoPaymentIdCommand(
                mercadoPagoWebhookDTO.data().id()
        );

        PaymentResponseDTO dto = this.controller.finalizePaymentByMercadoPago(
                command
        );

        return ResponseEntity.ok(dto);
    }
}
