package com.example.qlysachh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.qlysachh.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
}
