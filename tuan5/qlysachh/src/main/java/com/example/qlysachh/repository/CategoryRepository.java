package com.example.qlysachh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.qlysachh.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
}
