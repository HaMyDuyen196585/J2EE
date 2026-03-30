package com.example.hamyduyen_2280600508.controller;

import com.example.hamyduyen_2280600508.model.Order;
import com.example.hamyduyen_2280600508.model.OrderStatus;
import com.example.hamyduyen_2280600508.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ===== CUSTOMER VIEWS =====

    // Xem lịch sử đơn hàng của khách hàng
    @GetMapping("")
    public String viewMyOrders(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        List<Order> orders = orderService.getOrdersByCustomerEmail(email);

        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Lịch Sử Đơn Hàng");

        return "order/my-orders";
    }

    // Xem chi tiết đơn hàng
    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Authentication authentication, Model model) {
        Order order = orderService.getOrderById(id);

        if (order == null) {
            return "redirect:/orders";
        }

        // Kiểm tra quyền: chỉ chủ sở hữu đơn hàng mới được xem
        if (!order.getCustomerEmail().equals(authentication.getName())) {
            return "redirect:/403";
        }

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderService.getOrderDetails(id));
        model.addAttribute("pageTitle", "Chi Tiết Đơn Hàng #" + id);

        return "order/order-detail";
    }

    // ===== ADMIN VIEWS =====

    // Xem tất cả đơn hàng (admin only)
    @GetMapping("/admin/all")
    public String viewAllOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();

        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Quản Lý Đơn Hàng");

        return "order/admin-orders";
    }

    // Xem chi tiết đơn hàng (admin)
    @GetMapping("/admin/{id}")
    public String viewOrderDetailAdmin(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);

        if (order == null) {
            return "redirect:/orders/admin/all";
        }

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderService.getOrderDetails(id));
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("pageTitle", "Chi Tiết Đơn Hàng #" + id);

        return "order/admin-order-detail";
    }

    // Cập nhật trạng thái đơn hàng (admin)
    @PostMapping("/admin/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật trạng thái: " + e.getMessage());
        }

        return "redirect:/orders/admin/" + id;
    }
}
