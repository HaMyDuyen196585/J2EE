package com.example.hamyduyen_2280600508.controller;

import com.example.hamyduyen_2280600508.model.CartItem;
import com.example.hamyduyen_2280600508.model.Order;
import com.example.hamyduyen_2280600508.model.Product;
import com.example.hamyduyen_2280600508.service.CartService;
import com.example.hamyduyen_2280600508.service.OrderService;
import com.example.hamyduyen_2280600508.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // Get cart from session or create empty cart
    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    // View cart
    @GetMapping("")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        long totalPrice = cartService.getTotalPrice(cart);
        int totalQuantity = cartService.getTotalQuantity(cart);

        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("cartSize", cart.size());

        return "cart/view";
    }

    // Add to cart
    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại");
                return "redirect:/";
            }

            List<CartItem> cart = getCart(session);
            cart = cartService.addToCart(productId, quantity, cart);
            session.setAttribute("cart", cart);

            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm vào giỏ hàng thành công!");
            return "redirect:/cart";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return "redirect:/";
        }
    }

    // Update cart
    @PostMapping("/update/{productId}")
    public String updateCart(@PathVariable Long productId,
            @RequestParam int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            List<CartItem> cart = getCart(session);
            cart = cartService.updateCart(productId, quantity, cart);
            session.setAttribute("cart", cart);

            if (quantity <= 0) {
                redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Cập nhật giỏ hàng thành công!");
            }

            return "redirect:/cart";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật giỏ hàng: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    // Remove from cart
    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            List<CartItem> cart = getCart(session);
            cart = cartService.removeFromCart(productId, cart);
            session.setAttribute("cart", cart);

            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm khỏi giỏ hàng thành công!");
            return "redirect:/cart";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    // Clear cart
    @GetMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            List<CartItem> cart = cartService.clearCart();
            session.setAttribute("cart", cart);

            redirectAttributes.addFlashAttribute("success", "Xóa toàn bộ giỏ hàng thành công!");
            return "redirect:/cart";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa giỏ hàng: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    // Show checkout form
    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        List<CartItem> cart = getCart(session);

        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống. Vui lòng thêm sản phẩm trước!");
            return "redirect:/cart";
        }

        long totalPrice = cartService.getTotalPrice(cart);
        int totalQuantity = cartService.getTotalQuantity(cart);

        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("customerEmail", authentication != null ? authentication.getName() : "");

        return "checkout/form";
    }

    // Process checkout
    @PostMapping("/checkout")
    public String processCheckout(
            @RequestParam String customerName,
            @RequestParam String customerPhone,
            @RequestParam String customerAddress,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

            if (cart == null || cart.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống");
                return "redirect:/cart";
            }

            // Lấy email từ authentication (user đang đăng nhập)
            String customerEmail = authentication != null ? authentication.getName() : "";

            // Tạo order từ giỏ hàng
            Order order = orderService.createOrder(customerName, customerEmail,
                    customerPhone, customerAddress, cart);

            // Lưu order ID vào session để hiển thị trang xác nhận
            session.setAttribute("lastOrderId", order.getId());
            session.setAttribute("lastOrderTotal", order.getTotalAmount());

            // Xóa giỏ hàng sau khi checkout
            session.setAttribute("cart", new ArrayList<>());

            redirectAttributes.addFlashAttribute("success", "Đơn hàng của bạn đã được tạo thành công!");
            return "redirect:/cart/confirmation";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xử lý thanh toán: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    // Show order confirmation
    @GetMapping("/confirmation")
    public String showConfirmation(HttpSession session, Model model) {
        Long orderId = (Long) session.getAttribute("lastOrderId");
        Long orderTotal = (Long) session.getAttribute("lastOrderTotal");

        if (orderId != null) {
            model.addAttribute("orderId", orderId);
            model.addAttribute("orderTotal", orderTotal);
            // Clear session attributes
            session.removeAttribute("lastOrderId");
            session.removeAttribute("lastOrderTotal");
        }

        return "checkout/confirmation";
    }
}
