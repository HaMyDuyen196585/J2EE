package com.example.hamyduyen_2280600508.service;

import com.example.hamyduyen_2280600508.model.Product;
import com.example.hamyduyen_2280600508.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public List<Product> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Page<Product> getProductsByCategory(Integer categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    // Search with sorting
    public List<Product> searchAndSort(String keyword, Integer categoryId, String sortBy) {
        List<Product> products;

        if (keyword != null && !keyword.isEmpty()) {
            products = searchProducts(keyword);
        } else if (categoryId != null) {
            products = getProductsByCategory(categoryId);
        } else {
            products = getAllProducts();
        }

        // Apply sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            products = sortProducts(products, sortBy);
        }

        return products;
    }

    // Sort products by given criteria
    private List<Product> sortProducts(List<Product> products, String sortBy) {
        switch (sortBy) {
            case "name_asc":
                return products.stream()
                        .sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()))
                        .collect(Collectors.toList());
            case "name_desc":
                return products.stream()
                        .sorted((p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()))
                        .collect(Collectors.toList());
            case "price_asc":
                return products.stream()
                        .sorted((p1, p2) -> Long.compare(p1.getPrice(), p2.getPrice()))
                        .collect(Collectors.toList());
            case "price_desc":
                return products.stream()
                        .sorted((p1, p2) -> Long.compare(p2.getPrice(), p1.getPrice()))
                        .collect(Collectors.toList());
            default:
                return products;
        }
    }
}