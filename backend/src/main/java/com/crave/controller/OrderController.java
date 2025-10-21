package com.crave.controller;

import com.crave.dto.*;
import com.crave.service.OrderService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orders;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderResponse place(Principal user, @Valid @RequestBody OrderRequest request) {
    return orders.place(user.getName(), request);
  }

  @GetMapping("/my")
  public List<OrderResponse> mine(Principal user) {
    return orders.mine(user.getName());
  }

  @GetMapping
  public List<OrderResponse> all() {
    return orders.all();
  }

  @PutMapping("/{id}/status")
  public OrderResponse updateStatus(
      @PathVariable Long id, @Valid @RequestBody StatusRequest request) {
    return orders.updateStatus(id, request.status());
  }
}
