package com.store.soattechchallenge.shoppingCart.product.usecases.command;

import java.math.BigDecimal;

public record ProductRequestCommand(
        String productName,
        Long idCategory,
        BigDecimal unitPrice,
        Long preparationTime
) {}