package com.example.qlysachh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;

import com.example.qlysachh.model.Product;
import com.example.qlysachh.service.ProductService;
import com.example.qlysachh.service.CategoryService;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    private final CategoryService categoryService;
    
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "product/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/add";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/add";
        }
        return "redirect:/products";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product, 
                              @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                
                // 1. Lưu vào thư mục RUNTIME (để ảnh hiện ngay lập tức)
                String targetDir = new File("target/classes/static/images/").getAbsolutePath();
                Path targetPath = Paths.get(targetDir);
                if (!Files.exists(targetPath)) Files.createDirectories(targetPath);
                Files.copy(imageFile.getInputStream(), targetPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

                // 2. Lưu vào thư mục SOURCE (để không bị mất ảnh khi bro tắt app)
                String srcDir = new File("src/main/resources/static/images/").getAbsolutePath();
                Path srcPath = Paths.get(srcDir);
                if (!Files.exists(srcPath)) Files.createDirectories(srcPath);
                Files.copy(imageFile.getInputStream(), srcPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                
                // Lưu đường dẫn vào database
                product.setImage("/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}