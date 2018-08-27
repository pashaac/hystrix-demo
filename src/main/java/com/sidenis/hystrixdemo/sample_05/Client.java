package com.sidenis.hystrixdemo.sample_05;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class Client {

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
    }

    @HystrixCommand(groupKey = "HystrixDemoUsersRestCall"/*, commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10")}*/)
    String call() {
        return restTemplate.getForObject("https://jsonplaceholder.typicode.com/users", String.class);
    }

}




















