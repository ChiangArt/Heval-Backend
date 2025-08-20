package com.heval.ecommerce.dto.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductRequest(@NotBlank(message = "El título es obligatorio")
                             String title,

                             @NotBlank(message = "La descripción es obligatoria")
                             String description,

//                             @NotBlank(message = "La descripción Arquetipo es obligatoria")
//                             String descriptionArchetype,

                             @NotBlank(message = "El material es obligatoria")
                             String material,

                             @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
                             BigDecimal price,

//                           @NotEmpty(message = "Debes proporcionar al menos un color")
//                           List<@NotBlank(message = "El color no puede estar vacío") String> colors,

                             @NotBlank(message = "El color no puede estar vacío")
                             String color,

                             @Min(value = 0, message = "La cantidad debe ser mayor o igual a 0")
                             int quantity,


                             @Min(value = 0, message = "El porcentaje de descuento debe ser mayor o igual a 0")
                             Integer discountPercentage,

                             LocalDateTime discountUntil,

                             @NotEmpty(message = "Debes proporcionar al menos una imagen")
                             List<@NotBlank(message = "La URL no puede estar vacía") String> imageUrls,

                             @NotNull(message = "La colección es obligatoria")
                             Long collectionId) {
}




