package com.crave.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Entity @Getter @Setter
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Order order;
    // Snapshot values keep old orders readable after a dish is edited or deleted.
    private Long dishId;
    private String dishName;
    @Column(precision = 10, scale = 2) private BigDecimal price;
    private int quantity;
}
