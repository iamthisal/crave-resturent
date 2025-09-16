package com.crave.service;


import com.crave.dto.DishRequest;
import com.crave.model.Dish;
import com.crave.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service @RequiredArgsConstructor
public class DishService {
    private final DishRepository dishes;
    public List<Dish> list() { return dishes.findAll(); }
    public Dish get(Long id) { return dishes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found")); }
    public Dish save(Long id, DishRequest request) {
        Dish dish = id == null ? new Dish() : get(id);
        dish.setName(request.name().trim()); dish.setDescription(request.description().trim());
        dish.setPrice(request.price()); dish.setImageUrl(request.imageUrl());
        dish.setCategory(request.category().trim()); dish.setAvailable(request.available());
        return dishes.save(dish);
    }
    public void delete(Long id) { dishes.delete(get(id)); }
}
