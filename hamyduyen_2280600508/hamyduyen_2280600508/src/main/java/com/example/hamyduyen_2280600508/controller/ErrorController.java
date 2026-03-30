package com.example.hamyduyen_2280600508.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/403")
    public String access403() {
        return "403";
    }
}
