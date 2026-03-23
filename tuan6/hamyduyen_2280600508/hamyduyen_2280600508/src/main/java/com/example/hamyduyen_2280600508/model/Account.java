package com.example.hamyduyen_2280600508.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.*;

@Entity @Data
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String login_name;
    private String password;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "AccountRole",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}