package com.store.soattechchallenge.payment.application.gateways;

public record PaymentProductDTO(String name, String category, Double unitPrice, Integer quantity) {
    public Double getTotalValue() {
        return this.unitPrice * this.quantity;
    }
}
