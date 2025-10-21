package com.crave.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record DishRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 1000) String description,
    @NotNull @DecimalMin("0.01") @DecimalMax("99999999.99") @Digits(integer = 8, fraction = 2)
        BigDecimal price,
    @Size(max = 2000)
        @Pattern(regexp = "^(https?://.*)?$", message = "must be an http or https URL")
        String imageUrl,
    @NotBlank @Size(max = 60) String category,
    @NotNull Boolean available) {}
