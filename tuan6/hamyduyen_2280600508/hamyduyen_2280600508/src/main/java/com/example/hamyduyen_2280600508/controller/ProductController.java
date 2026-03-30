package com.example.hamyduyen_2280600508.controller;

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
    @GetMapping({"/", "/home"})
    public String showHomePage(Model model,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categoryId", required = false) Integer categoryId,
                               @RequestParam(value = "sort", required = false) String sort,
                               @RequestParam(value = "page", defaultValue = "1") int page) {
        final int PAGE_SIZE = 5;
        
        List<Product> products;

        // Filter by keyword or category
        if (keyword != null && !keyword.isEmpty()) {
            products = productService.searchProducts(keyword);
        } else if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
        } else {
            products = productService.getAllProducts();
        }

        // Sort by price
        if ("asc".equals(sort)) {
            products = productService.sortByPriceAsc(products);
        } else if ("desc".equals(sort)) {
            products = productService.sortByPriceDesc(products);
        }

        // Pagination
        int totalPages = productService.getTotalPages(products, PAGE_SIZE);
        products = productService.paginate(products, page, PAGE_SIZE);

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        return "client/home";
    }

    // --- TRANG QUẢN TRỊ (ADMIN) ---
    @GetMapping("/products")
    public String listProducts(Model model,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        List<Product> products;

        if (keyword != null && !keyword.isEmpty()) {
            products = productService.searchProducts(keyword);
        } else if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
        } else {
            products = productService.getAllProducts();
        }

        long totalProducts = products.size();
        long totalValue = products.stream().mapToLong(Product::getPrice).sum();

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("keyword", keyword);

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
                              @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();

                // Save to target/classes/static/images (running app directory)
                String targetDir = new File("target/classes/static/images/").getAbsolutePath();
                Path targetPath = Paths.get(targetDir);
                if (!Files.exists(targetPath)) Files.createDirectories(targetPath);
                Files.write(targetPath.resolve(fileName), imageFile.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                // Also save to src/main/resources/static/images (for IDE hot reload)
                String srcDir = new File("src/main/resources/static/images/").getAbsolutePath();
                Path srcPath = Paths.get(srcDir);
                if (!Files.exists(srcPath)) Files.createDirectories(srcPath);
                Files.write(srcPath.resolve(fileName), imageFile.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                product.setImage("/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (product.getId() != null) {
            Product existingProduct = productService.getProductById(product.getId());
            if (existingProduct != null) product.setImage(existingProduct.getImage());
        }

        productService.saveProduct(product);
        return "redirect:/products";
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
}