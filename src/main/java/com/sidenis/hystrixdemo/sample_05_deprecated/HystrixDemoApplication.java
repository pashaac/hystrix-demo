package com.sidenis.hystrixdemo.sample_05_deprecated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class HystrixDemoApplication {

  private static final Logger log = LoggerFactory.getLogger(HystrixDemoApplication.class);

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(HystrixDemoApplication.class, args);
    log.info(context.getBean(Client.class).call());
  }

}
