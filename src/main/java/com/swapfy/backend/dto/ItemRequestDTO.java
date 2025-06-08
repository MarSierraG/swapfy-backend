package com.swapfy.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class ItemRequestDTO {

    @NotBlank(message = "El título no puede estar vacío")
    private String title;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String description;

    @NotNull(message = "El valor en créditos es obligatorio")
    @Min(value = 0, message = "El valor en créditos no puede ser negativo")
    private Integer creditValue;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "offer|demand", message = "El tipo debe ser 'offer' o 'demand'")
    private String type;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "Available|Unavailable", message = "El estado debe ser 'Available' o 'Unavailable'")
    private String status;

    @NotEmpty(message = "Debes seleccionar al menos una etiqueta")
    private List<Long> tags;

    @Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
    private String imageUrl;

}
