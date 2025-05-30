package com.store.soattechchallenge.shoppingCart.order.domain.repository;

import com.store.soattechchallenge.shoppingCart.order.domain.model.Order;
import com.store.soattechchallenge.shoppingCart.order.infrastructure.adapters.in.dto.OrderResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepository {

    void save(Order Order);
    Optional<OrderResponseDTO>  findOrderById(String orderId);
    Page<OrderResponseDTO> findAll(Pageable pageable);
    String orderId();
}
