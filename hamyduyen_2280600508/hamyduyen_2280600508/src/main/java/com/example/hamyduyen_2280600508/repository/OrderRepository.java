package com.example.hamyduyen_2280600508.repository;

import com.example.hamyduyen_2280600508.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerEmailOrderByOrderDateDesc(String customerEmail);
}
