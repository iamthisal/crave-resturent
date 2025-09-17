package com.crave.dto;


import com.crave.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public record OrderResponse(Long id, String fullName, String phone, String deliveryAddress,
    String note, OrderStatus status, BigDecimal total, LocalDateTime createdAt, List<Item> items) {
    public record Item(Long dishId, String dishName, BigDecimal price, int quantity) { }
    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getFullName(), order.getPhone(), order.getDeliveryAddress(),
            order.getNote(), order.getStatus(), order.getTotal(), order.getCreatedAt(),
            order.getItems().stream().map(i -> new Item(i.getDishId(), i.getDishName(), i.getPrice(), i.getQuantity())).toList());
    }
}
