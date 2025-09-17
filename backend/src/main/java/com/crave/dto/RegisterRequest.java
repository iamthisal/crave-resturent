package com.crave.dto;


import jakarta.validation.constraints.*;
public record RegisterRequest(@NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 200) String email,
    @NotBlank @Size(min = 6, max = 72) String password) { }
