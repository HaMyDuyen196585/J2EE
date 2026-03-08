package com.example.qlysachh.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.example.qlysachh.model.Category;
import com.example.qlysachh.repository.CategoryRepository;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }
    
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }
    
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}