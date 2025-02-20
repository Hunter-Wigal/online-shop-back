package com.shop.online_shop.controllers;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;


@Component
@Endpoint(id="custom")
public class HealthController {
    @ReadOperation
    public String customEndpoint() {
        return "This is a custom endpoint";
    }
}
