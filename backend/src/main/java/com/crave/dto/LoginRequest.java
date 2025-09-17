package com.crave.dto;


import jakarta.validation.constraints.*;
public record LoginRequest(@NotBlank @Email String email, @NotBlank @Size(max = 72) String password) { }
