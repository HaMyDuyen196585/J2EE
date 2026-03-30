package com.example.hamyduyen_2280600508.service;

import com.example.hamyduyen_2280600508.model.Product;
import com.example.hamyduyen_2280600508.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Product> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    // Sort by price ascending
    public List<Product> sortByPriceAsc(List<Product> products) {
        return products.stream()
                .sorted((a, b) -> Long.compare(a.getPrice(), b.getPrice()))
                .collect(Collectors.toList());
    }

    // Sort by price descending
    public List<Product> sortByPriceDesc(List<Product> products) {
        return products.stream()
                .sorted((a, b) -> Long.compare(b.getPrice(), a.getPrice()))
                .collect(Collectors.toList());
    }

    // Pagination
    public List<Product> paginate(List<Product> products, int page, int pageSize) {
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, products.size());
        if (start >= products.size()) {
            return List.of();
        }
        return products.subList(start, end);
    }

    // Get total pages
    public int getTotalPages(List<Product> products, int pageSize) {
        return (products.size() + pageSize - 1) / pageSize;
    }
}