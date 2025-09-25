package com.crave.dto;


import com.crave.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
public record StatusRequest(@NotNull OrderStatus status) { }
