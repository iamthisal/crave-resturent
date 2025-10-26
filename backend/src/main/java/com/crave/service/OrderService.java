package com.crave.service;

import com.crave.dto.*;
import com.crave.model.*;
import com.crave.repository.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
  private final OrderRepository orders;
  private final UserRepository users;
  private final DishService dishes;

  public OrderResponse place(String email, OrderRequest request) {
    Order order = new Order();
    order.setUser(
        users
            .findByEmail(email)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")));
    order.setFullName(request.fullName().trim());
    order.setPhone(request.phone().trim());
    order.setDeliveryAddress(request.deliveryAddress().trim());
    order.setNote(request.note());
    BigDecimal total = BigDecimal.ZERO;
    for (OrderRequest.Item requested : request.items()) {
      Dish dish = dishes.get(requested.dishId());
      if (!dish.isAvailable())
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, dish.getName() + " is unavailable");
      OrderItem item = new OrderItem();
      item.setOrder(order);
      item.setDishId(dish.getId());
      item.setDishName(dish.getName());
      item.setPrice(dish.getPrice());
      item.setQuantity(requested.quantity());
      order.getItems().add(item);
      total = total.add(dish.getPrice().multiply(BigDecimal.valueOf(requested.quantity())));
    }
    order.setTotal(total);
    return OrderResponse.from(orders.save(order));
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> mine(String email) {
    return orders.findByUserEmailOrderByCreatedAtDesc(email).stream()
        .map(OrderResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> all() {
    return orders.findAllByOrderByCreatedAtDesc().stream().map(OrderResponse::from).toList();
  }

  public OrderResponse updateStatus(Long id, OrderStatus status) {
    Order order =
        orders
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    if (status.ordinal() != order.getStatus().ordinal() + 1)
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Order status must advance one step at a time");
    order.setStatus(status);
    return OrderResponse.from(order);
  }
}
