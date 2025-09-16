package com.crave.controller;


import com.crave.dto.DishRequest;
import com.crave.model.Dish;
import com.crave.service.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController @RequestMapping("/api/dishes") @RequiredArgsConstructor
public class DishController {
    private final DishService dishes;
    @GetMapping public List<Dish> list() { return dishes.list(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Dish create(@Valid @RequestBody DishRequest request) { return dishes.save(null, request); }
    @PutMapping("/{id}") public Dish update(@PathVariable Long id, @Valid @RequestBody DishRequest request) { return dishes.save(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { dishes.delete(id); }
}
