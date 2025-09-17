package com.crave.config;


import com.crave.model.*;
import com.crave.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
@Component @RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final DishRepository dishes;
    private final PasswordEncoder passwords;
    @Override @Transactional
    public void run(String... args) {
        if (!users.existsByEmail("admin@crave.com")) {
            User admin = new User(); admin.setName("Crave Admin"); admin.setEmail("admin@crave.com");
            admin.setPassword(passwords.encode("admin123")); admin.setRole(Role.ADMIN); users.save(admin);
        }
        if (dishes.count() == 0) {
            seed("Classic Beef Burger", "Juicy beef, melted cheddar, crisp lettuce and our house sauce.", "1450", "Burgers", "photo-1568901346375-23c9450c58cd");
            seed("Margherita Pizza", "Stone-baked pizza with tomato, mozzarella and fresh basil.", "2200", "Pizza", "photo-1574071318508-1cdbab80d002");
            seed("Creamy Chicken Pasta", "Penne tossed with grilled chicken and creamy garlic sauce.", "1850", "Pasta", "photo-1473093295043-cdd812d0e601");
            seed("Grilled Chicken Bowl", "Tender grilled chicken with rice, vegetables and a zesty dressing.", "1650", "Bowls", "photo-1546069901-ba9599a7e63c");
            seed("Chocolate Brownie", "A warm chocolate brownie for a sweet finish.", "650", "Desserts", "photo-1606313564200-e75d5e30476c");
            seed("Fresh Lime Cooler", "Fresh lime, a touch of sweetness and plenty of ice.", "450", "Drinks", "photo-1513558161293-cdaf765edfd7");
        }
    }
    private void seed(String name, String description, String price, String category, String photo) {
        Dish dish = new Dish(); dish.setName(name); dish.setDescription(description);
        dish.setPrice(new BigDecimal(price)); dish.setCategory(category);
        dish.setImageUrl("https://images.unsplash.com/" + photo + "?auto=format&fit=crop&w=700&q=80"); dishes.save(dish);
    }
}
