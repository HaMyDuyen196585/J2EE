package com.example.hamyduyen_2280600508.service;

import com.example.hamyduyen_2280600508.model.CartItem;
import com.example.hamyduyen_2280600508.model.Order;
import com.example.hamyduyen_2280600508.model.OrderDetail;
import com.example.hamyduyen_2280600508.model.OrderStatus;
import com.example.hamyduyen_2280600508.model.Product;
import com.example.hamyduyen_2280600508.repository.OrderRepository;
import com.example.hamyduyen_2280600508.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductService productService;

    // Hiển thị tất cả đơn hàng
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Lấy đơn hàng theo ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // Lấy đơn hàng theo email khách
    public List<Order> getOrdersByCustomerEmail(String email) {
        return orderRepository.findByCustomerEmailOrderByOrderDateDesc(email);
    }

    // Tạo đơn hàng từ giỏ hàng
    public Order createOrder(String customerName, String customerEmail,
            String customerPhone, String customerAddress,
            List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống");
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerEmail(customerEmail);
        order.setCustomerPhone(customerPhone);
        order.setCustomerAddress(customerAddress);

        long totalAmount = 0;

        // Tạo OrderDetail từ CartItem
        for (CartItem cartItem : cartItems) {
            Product product = productService.getProductById(cartItem.getProductId());
            if (product == null) {
                continue;
            }

            OrderDetail detail = new OrderDetail(product, cartItem.getQuantity(), cartItem.getPrice());
            order.addOrderDetail(detail);
            totalAmount += detail.getSubtotal();
        }

        order.setTotalAmount(totalAmount);

        // Lưu order
        return orderRepository.save(order);
    }

    // Cập nhật trạng thái đơn hàng
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        if (order != null) {
            try {
                order.setStatus(Enum.valueOf(OrderStatus.class, status));
                return orderRepository.save(order);
            } catch (IllegalArgumentException e) {
                return order;
            }
        }
        return null;
    }

    // Xóa đơn hàng
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    // Lấy chi tiết đơn hàng
    public List<OrderDetail> getOrderDetails(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
