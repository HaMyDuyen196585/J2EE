package com.example.hamyduyen_2280600508.service;

import com.example.hamyduyen_2280600508.model.Order;
import com.example.hamyduyen_2280600508.model.OrderDetail;
import com.example.hamyduyen_2280600508.model.CartItem;
import com.example.hamyduyen_2280600508.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    public Order createOrder(List<CartItem> cartItems) {
        Order order = new Order();
        long totalAmount = 0;

        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(productService.getProductById(item.getId()));
            detail.setPrice(item.getPrice());
            detail.setQuantity(item.getQuantity());
            order.getOrderDetails().add(detail);
            totalAmount += item.getTotalPrice();
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getOrdersByAccountId(Long accountId) {
        return orderRepository.findByAccountId(accountId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void updateOrderPaidStatus(Long orderId, boolean isPaid) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setPaid(isPaid);
            orderRepository.save(order);
        }
    }
}
