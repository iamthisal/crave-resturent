package com.crave.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record OrderRequest(
    @NotBlank @Size(max = 100) String fullName,
    @NotBlank @Pattern(regexp = "[+0-9() \-]{7,25}", message = "must be a valid phone number") String phone,
    @NotBlank @Size(max = 500) String deliveryAddress,
    @Size(max = 1000) String note,
    @NotEmpty @Size(max = 100) List<@NotNull @Valid Item> items) {
    public record Item(@NotNull @Positive Long dishId, @Min(1) @Max(99) int quantity) { }
}
