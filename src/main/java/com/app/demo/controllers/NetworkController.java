package com.app.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Map;

@RestController
public class NetworkController {

    @GetMapping("/api/v1/network-info")
    public Map<String, String> networkInfo() {
        String ip = "127.0.0.1";
        try {
            ip = Collections.list(java.net.NetworkInterface.getNetworkInterfaces()).stream()
                .flatMap(ni -> Collections.list(ni.getInetAddresses()).stream())
                .filter(addr -> addr instanceof java.net.Inet4Address)
                .filter(addr -> !addr.isLoopbackAddress())
                .map(java.net.InetAddress::getHostAddress)
                .findFirst()
                .orElse("127.0.0.1");
        } catch (Exception e) {
            // fallback
        }
        return Map.of("ip", ip, "port", "8080");
    }
}

