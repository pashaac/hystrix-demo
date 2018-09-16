package com.sidenis.hystrixdemo.sample_06_deprecated;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
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

    String call() {
        HystrixTask hystrixTask = new HystrixTask();
        return hystrixTask.execute();
    }

    class HystrixTask extends HystrixCommand<String> {

        HystrixTask() {
            super(HystrixCommandGroupKey.Factory.asKey("HystrixDemoUsersRestCall"), 500);
        }

        @Override
        protected String run() throws Exception {
//            throw new RuntimeException();
            return restTemplate.getForObject("https://jsonplaceholder.typicode.com/users", String.class);
        }

        @Override
        protected String getFallback() {
            return "Oops, our application temporary unavailable (=^.^=)";
        }
    }
}




















