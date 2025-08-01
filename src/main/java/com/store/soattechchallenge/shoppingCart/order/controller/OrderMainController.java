package com.store.soattechchallenge.shoppingCart.order.controller;

import com.store.soattechchallenge.shoppingCart.order.domain.entities.Order;
import com.store.soattechchallenge.shoppingCart.order.gateways.EventPublisherGateway;
import com.store.soattechchallenge.shoppingCart.order.gateways.OrderRepositoryGateways;
import com.store.soattechchallenge.shoppingCart.order.infrastructure.gateways.StreamEventPublisherGateway;
import com.store.soattechchallenge.shoppingCart.order.infrastructure.mappers.OrderMapper;
import com.store.soattechchallenge.shoppingCart.order.presenters.OrderHttpPresenter;
import com.store.soattechchallenge.shoppingCart.order.usecases.CreateOrderUseCase;
import com.store.soattechchallenge.shoppingCart.order.usecases.FindOrdersUseCase;
import com.store.soattechchallenge.shoppingCart.order.usecases.UpdateOrderUseCase;
import com.store.soattechchallenge.shoppingCart.order.usecases.command.OrderRequestCommand;
import com.store.soattechchallenge.shoppingCart.order.infrastructure.api.dto.OrderRequestDTO;
import com.store.soattechchallenge.shoppingCart.order.infrastructure.api.dto.OrderResponseDTO;
import com.store.soattechchallenge.utils.exception.CustomException;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


public class OrderMainController {

    public final OrderRepositoryGateways orderRepositoryGateways;
    public final OrderMapper orderMapper;
    private final EventPublisherGateway eventPublisherGateway;


    public OrderMainController(
            OrderRepositoryGateways orderRepositoryGateways,
            OrderMapper orderMapper,
            StreamBridge streamBridge
    ) {
        this.orderRepositoryGateways = orderRepositoryGateways;
        this.orderMapper = orderMapper;
        this.eventPublisherGateway = new StreamEventPublisherGateway(streamBridge);
    }

    public Optional<OrderResponseDTO> createProduct(OrderRequestCommand command) {
        CreateOrderUseCase createOrderUseCase = new CreateOrderUseCase(this.orderRepositoryGateways, this.eventPublisherGateway);
        OrderHttpPresenter orderHttpPresenter = new OrderHttpPresenter(this.orderMapper);
        Optional<Order>order = createOrderUseCase.saveOrder(command);
        return orderHttpPresenter.execute(order);
    }

    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        FindOrdersUseCase findOrdersUseCase = new FindOrdersUseCase(this.orderRepositoryGateways);
        OrderHttpPresenter orderHttpPresenter = new OrderHttpPresenter(this.orderMapper);
        Page<Order> jpaOrderEntity =  findOrdersUseCase.getAllOrders(pageable);
        return orderHttpPresenter.execute(jpaOrderEntity);
    }

    public Optional<OrderResponseDTO> getOrdeById(String id) {
        FindOrdersUseCase findOrdersUseCase = new FindOrdersUseCase(this.orderRepositoryGateways);
        OrderHttpPresenter orderHttpPresenter = new OrderHttpPresenter(this.orderMapper);
        Optional<Order> jpaOrderEntity =  findOrdersUseCase.getOrdeById(id);
        return orderHttpPresenter.execute(jpaOrderEntity);
    }

    public Optional<OrderResponseDTO> updateOrder(String id, OrderRequestDTO orderRequestDTO){
        UpdateOrderUseCase updateOrderUseCase = new UpdateOrderUseCase(this.orderRepositoryGateways);
        OrderHttpPresenter orderHttpPresenter = new OrderHttpPresenter(this.orderMapper);

         getOrdeById(id).orElseThrow(() -> new CustomException(
                "Pedido não encontrado",
                HttpStatus.NOT_FOUND,
                String.valueOf(HttpStatus.NOT_FOUND.value()),
                LocalDateTime.now(),
                UUID.randomUUID()
        ));

        Optional<Order> jpaOrderEntity =  updateOrderUseCase.updateOrder(id, orderRequestDTO);
        return orderHttpPresenter.execute(jpaOrderEntity);
    }
}
