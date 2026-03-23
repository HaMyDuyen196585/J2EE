package com.example.hamyduyen_2280600508.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity @Data
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
}