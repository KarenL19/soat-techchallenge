package com.store.soattechchallenge.shoppingCart.order.infrastructure.api.dto;

import java.util.List;

public class OrderRequestDTO {
    private String clientId;
    private List<ProductRequest> products;

    public OrderRequestDTO() {
    }

    public OrderRequestDTO(String clientId, List<ProductRequest> products) {
        this.clientId = clientId;
        this.products = products;
    }

    public String getClientId() {
        return clientId;
    }

    public List<ProductRequest> getProducts() {
        return products;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setProducts(List<ProductRequest> products) {
        this.products = products;
    }
}