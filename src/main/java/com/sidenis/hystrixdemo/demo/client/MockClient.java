package com.sidenis.hystrixdemo.demo.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Size;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MockClient {

    private static final Random random = new Random();

    public String call(int error, int latency) {
        try {
            TimeUnit.MILLISECONDS.sleep(latency );
        } catch (InterruptedException e) {
            log.warn("Thread was interrupted...");
        }
        if (Math.random() * 100 < error) {
            throw new RuntimeException("External system threw an error");
        }
        return "OK";
    }
}
