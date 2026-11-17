package com.example.driverservice;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drivers")
public class DriverController {
    private static final Logger log = LoggerFactory.getLogger(DriverController.class);

    @GetMapping("/{id}")
    public String getDriver(@PathVariable String id, HttpServletRequest request) {
        int port = request.getLocalPort();
        log.info("ROUND-ROBIN: Request for driver {} handled by Driver Service on port {}", id, port);
        return "Driver ID: " + id + " | Served by Driver Service on port: " + port;
    }

    @GetMapping("/health")
    public String health(HttpServletRequest request) {
        return "Driver Service is UP on port: " + request.getLocalPort();
    }
}
