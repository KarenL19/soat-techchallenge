package com.store.soattechchallenge.payment.usecases;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.http.HttpStatus;
import com.store.soattechchallenge.payment.domain.entities.Payment;
import com.store.soattechchallenge.payment.gateways.exceptions.EntityNotFoundException;
import com.store.soattechchallenge.payment.gateways.PaymentRepositoryGateway;
import com.store.soattechchallenge.payment.usecases.commands.RenderQrCodeCommand;
import com.store.soattechchallenge.utils.exception.CustomException;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.UUID;

public class RenderQrCodeUseCase {
    private final PaymentRepositoryGateway paymentRepositoryGateway;
    private final QRCodeWriter qrCodeWriter;

    public RenderQrCodeUseCase(
            PaymentRepositoryGateway paymentRepositoryGateway,
            QRCodeWriter qrCodeWriter
    ) {
        this.paymentRepositoryGateway = paymentRepositoryGateway;
        this.qrCodeWriter = qrCodeWriter;
    }

    public BufferedImage execute(RenderQrCodeCommand command) {
        try {
            Payment payment = this.paymentRepositoryGateway.findById(command.id());
            BitMatrix bitMatrix = this.qrCodeWriter.encode(
                    payment.getQrCode(),
                    BarcodeFormat.QR_CODE,
                    command.width(),
                    command.height()
            );
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (EntityNotFoundException error) {
            throw new CustomException(
                    "Payment not found",
                    HttpStatus.NOT_FOUND,
                    String.valueOf(HttpStatus.NOT_FOUND.value()),
                    LocalDateTime.now(),
                    UUID.randomUUID()
            );
        } catch (WriterException e) {
            throw new CustomException(
                    "Error generation QR Code: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    LocalDateTime.now(),
                    UUID.randomUUID()
            );
        }
    }
}
