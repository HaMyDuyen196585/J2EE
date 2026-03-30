package com.example.hamyduyen_2280600508.controller;

import com.example.hamyduyen_2280600508.model.Account;
import com.example.hamyduyen_2280600508.model.Order;
import com.example.hamyduyen_2280600508.service.CartService;
import com.example.hamyduyen_2280600508.service.OrderService;
import com.example.hamyduyen_2280600508.service.AccountService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class OrderController {
    private final CartService cartService;
    private final OrderService orderService;
    private final AccountService accountService;

    public OrderController(CartService cartService, OrderService orderService, AccountService accountService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.accountService = accountService;
    }

    // Checkout - Only for non-admin users
    @PostMapping("/checkout")
    public String checkout(Authentication authentication, Model model) {
        // Check if user is admin - admins cannot checkout
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(auth -> auth.equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                return "redirect:/products"; // Redirect admin to products page
            }
        }

        if (cartService.getItems().isEmpty()) {
            return "redirect:/cart/view";
        }

        Order order = orderService.createOrder(cartService.getItems());
        
        // Set account if user is logged in
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            Account account = accountService.findByUsername(authentication.getName());
            if (account != null) {
                order.setAccount(account);
                orderService.updateOrderPaidStatus(order.getId(), false);
            }
        }

        // Clear the cart after checkout
        cartService.clear();

        model.addAttribute("order", order);
        return "cart/checkout-success";
    }

    // View order
    @GetMapping("/order/{id}")
    public String viewOrder(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            model.addAttribute("order", order);
            return "cart/order-detail";
        }
        return "redirect:/cart/view";
    }
}
