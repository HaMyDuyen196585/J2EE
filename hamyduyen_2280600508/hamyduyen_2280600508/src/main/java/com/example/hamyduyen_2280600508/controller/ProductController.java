package com.example.hamyduyen_2280600508.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.example.hamyduyen_2280600508.model.Product;
import com.example.hamyduyen_2280600508.service.ProductService;
import com.example.hamyduyen_2280600508.service.CategoryService;

@Controller
@RequestMapping("") // Để trống để nhận cả trang chủ / và /products
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // --- TRANG DÀNH CHO KHÁCH HÀNG (HOME) ---
    @GetMapping({ "/", "/home" })
    public String showHomePage(Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.getAllProducts(pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "client/home";
    }

    // --- TRANG QUẢN TRỊ (ADMIN) ---
    @GetMapping("/products")
    public String listProducts(Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        if (keyword != null && !keyword.isEmpty()) {
            productPage = productService.searchProducts(keyword, pageable);
        } else if (categoryId != null) {
            productPage = productService.getProductsByCategory(categoryId, pageable);
        } else {
            productPage = productService.getAllProducts(pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);

        return "product/list";
    }

    @GetMapping("/products/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/add";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/add";
        }
        return "redirect:/products";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute("product") Product product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = imageFile.getOriginalFilename();
                byte[] fileBytes = imageFile.getBytes();

                // Save to target/classes/static/images/
                String targetDir = new File("target/classes/static/images/").getAbsolutePath();
                Path targetPath = Paths.get(targetDir);
                if (!Files.exists(targetPath)) {
                    Files.createDirectories(targetPath);
                }
                Files.write(targetPath.resolve(fileName), fileBytes, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);

                // Save to src/main/resources/static/images/
                String srcDir = new File("src/main/resources/static/images/").getAbsolutePath();
                Path srcPath = Paths.get(srcDir);
                if (!Files.exists(srcPath)) {
                    Files.createDirectories(srcPath);
                }
                Files.write(srcPath.resolve(fileName), fileBytes, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);

                product.setImage("/images/" + fileName);
            } else if (product.getId() != null) {
                // Nếu không upload ảnh mới, giữ ảnh cũ
                Product existingProduct = productService.getProductById(product.getId());
                if (existingProduct != null) {
                    product.setImage(existingProduct.getImage());
                }
            }

            productService.saveProduct(product);
            return "redirect:/products";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/products?error=true";
        }
    }

    // --- ĐÃ FIX LỖI Ở HÀM NÀY ---
    @GetMapping("/products/detail/{id}")
    public String showProductDetail(@PathVariable("id") Long id, Model model) {
        // Dùng productService thay vì productRepository
        Product product = productService.getProductById(id);

        if (product == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có ID: " + id);
        }

        model.addAttribute("product", product);
        return "product/detail"; // Trả về giao diện detail.html
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    // --- SEARCH FOR CLIENT ---
    @GetMapping("/search")
    public String searchProducts(Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        List<Product> products = productService.searchAndSort(keyword, categoryId, sortBy);

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("totalResults", products.size());

        return "client/search-results";
    }
}