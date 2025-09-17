package com.crave.controller;


import com.crave.dto.*;
import com.crave.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.security.Principal;
import java.util.List;
@RestController @RequestMapping("/api/orders") @RequiredArgsConstructor
public class OrderController {
    private final OrderService orders;
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(Principal user, @Valid @RequestBody OrderRequest request) { return orders.place(user.getName(), request); }
    @GetMapping("/my") public List<OrderResponse> mine(Principal user) { return orders.mine(user.getName()); }
    @GetMapping public List<OrderResponse> all() { return orders.all(); }
}
