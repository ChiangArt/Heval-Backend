package com.heval.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CollectionRequest(

        @NotBlank(message = "El nombre no puede estar vacío")
        String name,

        String headlineTitle,

        String descriptionLine1,

        String descriptionLine2

) {
}
