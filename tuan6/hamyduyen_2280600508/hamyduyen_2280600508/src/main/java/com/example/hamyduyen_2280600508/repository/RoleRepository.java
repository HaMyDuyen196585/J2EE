package com.example.hamyduyen_2280600508.repository;

import com.example.hamyduyen_2280600508.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Phải có dòng này thì AccountController mới gọi được nhé bro
    Role findByName(String name);
}