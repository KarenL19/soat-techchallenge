package com.store.soattechchallenge.preparation.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PreparationCreateRequestDTO(
        @NotBlank
        String id,
        @NotNull
        @Positive
        Integer preparationTime
) {
}
