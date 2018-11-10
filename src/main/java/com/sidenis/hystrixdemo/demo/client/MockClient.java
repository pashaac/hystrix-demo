package com.sidenis.hystrixdemo.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MockClient {

  private static final Logger log = LoggerFactory.getLogger(MockClient.class);

  public String call(int error, int latency) {
    try {
      TimeUnit.MILLISECONDS.sleep(latency);
    } catch (InterruptedException e) {
      log.warn("Thread was interrupted...");
    }
    if (Math.random() * 100 < error) {
      throw new RuntimeException("External system threw an error");
    }
    return "OK";
  }
}
