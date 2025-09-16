package com.crave.repository;


import com.crave.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DishRepository extends JpaRepository<Dish, Long> { }
