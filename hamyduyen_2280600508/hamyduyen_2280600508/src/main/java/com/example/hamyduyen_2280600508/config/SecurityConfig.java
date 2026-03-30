package com.example.hamyduyen_2280600508.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                // Mở cửa cho trang chủ, trang chi tiết và đăng ký để khách (chưa login) vẫn bấm
                // xem sp được
                .requestMatchers("/", "/home", "/products/detail/**", "/images/**", "/static/**", "/register",
                        "/search", "/403")
                .permitAll()

                // Chỉ ADMIN mới được vào quản trị
                .requestMatchers(
                        "/products", "/products/add", "/products/edit/**", "/products/delete/**", "/products/save",
                        "/categories", "/categories/add", "/categories/edit/**", "/categories/delete/**",
                        "/orders/admin/**")
                .hasRole("ADMIN")

                // User đã login có thể xem cart, orders, checkout của mình
                .requestMatchers("/cart/**", "/orders", "/orders/**", "/checkout/**").authenticated()

                .anyRequest().authenticated())
                // SỬA QUAN TRỌNG: Đăng nhập xong nhảy về Home (Cửa hàng) thay vì lao thẳng vào
                // kho
                .formLogin(form -> form.defaultSuccessUrl("/home", true))
                .logout(logout -> logout.logoutSuccessUrl("/home"))
                .exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }
}