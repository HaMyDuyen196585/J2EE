package com.example.tuan4.controller;

import com.example.tuan4.model.Product;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 1. Hiển thị danh sách sản phẩm
    @GetMapping
    public String index(Model model) {
        model.addAttribute("listproduct", productService.getAll());
        return "product/products";
    }

    // 2. Hiển thị form thêm sản phẩm mới
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("product", new Product());
        return "product/create";
    }

    // 3. Xử lý lưu sản phẩm khi nhấn nút Save
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("product") Product product, 
                         BindingResult result, 
                         @RequestParam("imageProduct") MultipartFile imageProduct, 
                         Model model) {
        // Nếu có lỗi Validation (trống tên, sai giá...)
        if (result.hasErrors()) {
            return "product/create";
        }

        // Nếu không lỗi thì lưu ảnh và thêm vào danh sách
        productService.updateImage(product, imageProduct);
        productService.add(product);
        return "redirect:/products";
    }
}