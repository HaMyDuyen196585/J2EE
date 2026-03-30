package com.example.hamyduyen_2280600508.repository;

import com.example.hamyduyen_2280600508.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    // Câu lệnh JPQL để tìm tài khoản theo tên đăng nhập
    @Query("SELECT a FROM Account a WHERE a.login_name = :login_name")
    Optional<Account> findByLoginName(@Param("login_name") String login_name);
}