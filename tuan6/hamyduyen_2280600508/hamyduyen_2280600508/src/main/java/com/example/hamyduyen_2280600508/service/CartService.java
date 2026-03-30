package com.example.hamyduyen_2280600508.service;

import com.example.hamyduyen_2280600508.model.CartItem;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope
public class CartService {
    private List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public void addToCart(Long productId, String name, String image, long price, int quantity) {
        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            items.add(new CartItem(productId, name, image, price, quantity));
        }
    }

    public void updateQuantity(Long productId, int quantity) {
        items.stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
    }

    public void removeFromCart(Long productId) {
        items.removeIf(item -> item.getId().equals(productId));
    }

    public void clear() {
        items.clear();
    }

    public long getTotal() {
        return items.stream().mapToLong(CartItem::getTotalPrice).sum();
    }

    public int getCartSize() {
        return items.size();
    }
}
