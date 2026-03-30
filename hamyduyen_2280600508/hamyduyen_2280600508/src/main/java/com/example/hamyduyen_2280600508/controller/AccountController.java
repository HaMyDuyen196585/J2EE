package com.example.hamyduyen_2280600508.controller;

import com.example.hamyduyen_2280600508.model.Account;
import com.example.hamyduyen_2280600508.model.Role;
import com.example.hamyduyen_2280600508.repository.AccountRepository;
import com.example.hamyduyen_2280600508.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {

    // Đã xóa @Autowired và dùng `final` theo đúng chuẩn Clean Code
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // SonarLint sẽ rất thích cách tiêm Dependency qua Constructor như thế này
    public AccountController(AccountRepository accountRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("account", new Account());
        return "account/register";
    }

    @PostMapping("/register")
    public String registerUser(Account account, Model model) {
        // Kiểm tra trùng tên đăng nhập
        if (accountRepository.findByLoginName(account.getLogin_name()).isPresent()) {
            model.addAttribute("error", "Tên đăng nhập này đã có người sử dụng!");
            return "account/register";
        }

        // Băm mật khẩu
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        // Cấp quyền USER
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
        }
        account.getRoles().add(userRole);

        // Lưu xuống DB
        accountRepository.save(account);
        
        return "redirect:/login"; 
    }
}