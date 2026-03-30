package com.example.hamyduyen_2280600508.service;

import com.example.hamyduyen_2280600508.model.CartItem;
import com.example.hamyduyen_2280600508.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private ProductService productService;

    // Add product to cart
    public List<CartItem> addToCart(Long productId, int quantity, List<CartItem> cart) {
        if (cart == null) {
            cart = new ArrayList<>();
        }

        Product product = productService.getProductById(productId);
        if (product == null) {
            return cart;
        }

        // Check if product already exists in cart
        Optional<CartItem> existingItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Increase quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // Add new item
            CartItem cartItem = new CartItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    quantity,
                    product.getImage());
            cart.add(cartItem);
        }

        return cart;
    }

    // Update cart item quantity
    public List<CartItem> updateCart(Long productId, int quantity, List<CartItem> cart) {
        if (cart == null) {
            return cart;
        }

        Optional<CartItem> item = cart.stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId))
                .findFirst();

        if (item.isPresent()) {
            if (quantity <= 0) {
                // Remove item if quantity is 0 or less
                cart.remove(item.get());
            } else {
                item.get().setQuantity(quantity);
            }
        }

        return cart;
    }

    // Remove item from cart
    public List<CartItem> removeFromCart(Long productId, List<CartItem> cart) {
        if (cart == null) {
            return cart;
        }

        cart.removeIf(item -> item.getProductId().equals(productId));
        return cart;
    }

    // Clear cart
    public List<CartItem> clearCart() {
        return new ArrayList<>();
    }

    // Get total price
    public long getTotalPrice(List<CartItem> cart) {
        if (cart == null) {
            return 0;
        }

        return cart.stream()
                .mapToLong(CartItem::getTotalPrice)
                .sum();
    }

    // Get total quantity
    public int getTotalQuantity(List<CartItem> cart) {
        if (cart == null) {
            return 0;
        }

        return cart.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // Get cart size
    public int getCartSize(List<CartItem> cart) {
        if (cart == null) {
            return 0;
        }

        return cart.size();
    }
}
