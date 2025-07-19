package com.store.soattechchallenge.payment.infrastructure.mappers;

import com.store.soattechchallenge.payment.gateways.PaymentProductDTO;
import com.store.soattechchallenge.payment.infrastructure.api.dto.PaymentProductCreateRequestDTO;

public class PaymentProductMapper {
    public PaymentProductDTO apiCreateDTOToGatewayDTO(PaymentProductCreateRequestDTO paymentProductCreateRequestDTO) {
        return new PaymentProductDTO(
                paymentProductCreateRequestDTO.name(),
                paymentProductCreateRequestDTO.category(),
                paymentProductCreateRequestDTO.unitPrice(),
                paymentProductCreateRequestDTO.quantity()
        );
    }
}
