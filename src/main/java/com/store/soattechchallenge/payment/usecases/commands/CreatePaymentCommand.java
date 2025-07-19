package com.store.soattechchallenge.payment.usecases.commands;

import com.store.soattechchallenge.payment.gateways.PaymentProductDTO;

import java.util.List;

public record CreatePaymentCommand(String id, Double vlTotalPedido, List<PaymentProductDTO> products) {
}
