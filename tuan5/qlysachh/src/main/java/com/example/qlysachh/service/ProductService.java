package com.example.qlysachh.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.example.qlysachh.model.Product;
import com.example.qlysachh.repository.ProductRepository;

@Service
public class ProductService {
    private ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public void saveProduct(Product product) {
        productRepository.save(product);
    }
    
    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }
    
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }
}