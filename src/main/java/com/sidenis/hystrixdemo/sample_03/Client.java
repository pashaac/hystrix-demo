package com.sidenis.hystrixdemo.sample_03;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Slf4j
public class Client {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static class HystrixTask extends HystrixCommand<String> {

        HystrixTask() {
            super(HystrixCommandGroupKey.Factory.asKey("HystrixDemoUsersRestCall"));
        }

        @Override
        protected String run() throws Exception {
            return restTemplate.getForObject("https://jsonplaceholder.typicode.com/users/1", String.class);
        }
    }

    public static void main(String[] args) {
        HystrixTask hystrixTask = new HystrixTask();
        log.info(hystrixTask.execute());
    }

}




// "Oops, system temporary unavailable ¯\\_(ツ)_/¯"