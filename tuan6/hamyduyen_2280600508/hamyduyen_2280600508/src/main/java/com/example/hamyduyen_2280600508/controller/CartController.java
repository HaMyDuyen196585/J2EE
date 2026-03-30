package com.example.hamyduyen_2280600508.controller;

import com.example.hamyduyen_2280600508.model.Product;
import com.example.hamyduyen_2280600508.service.CartService;
import com.example.hamyduyen_2280600508.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    // Add to cart
    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Long id,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity) {
        Product product = productService.getProductById(id);
        if (product != null) {
            cartService.addToCart(product.getId(), product.getName(), 
                                product.getImage(), product.getPrice(), quantity);
        }
        return "redirect:/cart/view";
    }

    // View cart
    @GetMapping("/view")
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("total", cartService.getTotal());
        model.addAttribute("cartSize", cartService.getCartSize());
        return "cart/list";
    }

    // Update quantity
    @PostMapping("/update/{id}")
    public String updateQuantity(@PathVariable("id") Long id,
                                 @RequestParam("quantity") int quantity) {
        if (quantity > 0) {
            cartService.updateQuantity(id, quantity);
        } else {
            cartService.removeFromCart(id);
        }
        return "redirect:/cart/view";
    }

    // Remove from cart
    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long id) {
        cartService.removeFromCart(id);
        return "redirect:/cart/view";
    }

    // Clear cart
    @GetMapping("/clear")
    public String clearCart() {
        cartService.clear();
        return "redirect:/cart/view";
    }
}
