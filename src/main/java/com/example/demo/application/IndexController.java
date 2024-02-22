package com.example.demo.application;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public String index(){
        // src/main/resources/
        return "index";
    }

    @PreAuthorize("hasRole('READ')")
    @GetMapping("/anyone")
    public String anyone(){
        return "anyone";
    }

    @PreAuthorize("hasRole('WRITE')")
    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }
}
