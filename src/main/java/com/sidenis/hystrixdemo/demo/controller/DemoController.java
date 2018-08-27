package com.sidenis.hystrixdemo.demo.controller;

import com.netflix.hystrix.*;
import com.sidenis.hystrixdemo.demo.client.MockClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private final MockClient client;

    @Autowired
    public DemoController(MockClient client) {
        this.client = client;
    }

    @GetMapping("/dangerous")
    String dangerous(@RequestParam(value = "error", required = false, defaultValue = "0") int error,
                     @RequestParam(value = "latency", required = false, defaultValue = "0") int latency) {
        return client.call(error, latency);
    }

    @GetMapping("/securely")
    String securely(@RequestParam(value = "error", required = false, defaultValue = "0") int error,
                    @RequestParam(value = "latency", required = false, defaultValue = "0") int latency) {
        HystrixCommand<String> hystrixCommand = new HystrixCommand<String>(configuration()) {
            @Override
            protected String run() throws Exception {
                return client.call(error, latency);
            }
        };
        return hystrixCommand.execute();
    }

    private HystrixCommand.Setter configuration() {
        return HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("HystrixDemo"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerEnabled(false));
    }
}
