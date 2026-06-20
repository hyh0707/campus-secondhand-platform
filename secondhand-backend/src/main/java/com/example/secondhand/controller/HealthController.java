package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("secondhand-backend is running");
    }
}