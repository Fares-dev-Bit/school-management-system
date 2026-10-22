package com.app.demo.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShutdownController {

    private final ApplicationContext context;

    public ShutdownController(ApplicationContext context) {
        this.context = context;
    }

    @PostMapping("/api/v1/shutdown")
    public void shutdown() {
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            SpringApplication.exit(context, () -> 0);
            System.exit(0);
        }).start();
    }
}
