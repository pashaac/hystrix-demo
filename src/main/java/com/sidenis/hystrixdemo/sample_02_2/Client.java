package com.sidenis.hystrixdemo.sample_02_2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;

@Slf4j
@Component
public class Client {

    private static final int TIMEOUT = 1000;

    private static Random random = new Random();
    private static RestTemplate restTemplate = new RestTemplate();

    private BlockingQueue<Runnable> queue;
    private ExecutorService service;

    @PostConstruct
    public void init() {
        queue = new LinkedBlockingQueue<>();
        service = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, queue);
    }

    @PreDestroy
    public void cleanUp() {
        service.shutdown();
    }

    private Future<String> call(String url) {
        return service.submit(() -> {
            Thread.sleep((long) (TIMEOUT * random.nextDouble()));
            return restTemplate.getForObject(url, String.class);
        });
    }

    private Optional<String> call(Future<String> future) {
        try {
            return Optional.ofNullable(future.get(TIMEOUT, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            log.error("Task execution was interrupted, message: {}", e.getMessage());
            return Optional.empty();
        } catch (ExecutionException e) {
            log.error("Execution exception during task execution, message: {}", e.getMessage());
            return Optional.empty();
        } catch (TimeoutException e) {
            log.error("Timeout exception during task execution, message: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Scheduled(fixedDelay = 100)
    void load() {
        call("https://jsonplaceholder.typicode.com/users");
    }

    @Scheduled(fixedDelay = 500)
    void queueChecker() {
        log.info("Queue size: {}", queue.size());
    }

}