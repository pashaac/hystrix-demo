package com.sidenis.hystrixdemo.sample_03;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.*;

@Slf4j
@Component
public class Client {

    private BlockingQueue<Runnable> queue;
    private ExecutorService service;
    private Random random;
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        random = new Random();
        queue = new LinkedBlockingQueue<>();
        service = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, queue);
    }

    @PreDestroy
    public void cleanUp() {
        service.shutdown();
    }

    private Future<String> call(String url) {
        return service.submit(() -> {
            Thread.sleep((long) (1000 * random.nextDouble()));
            return restTemplate.getForObject(url, String.class);
        });
    }

    @Nullable
    private String call(Future<String> future) {
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Task execution was interrupted, message: {}", e.getMessage());
            return null;
        } catch (ExecutionException e) {
            log.error("Execution exception during task execution, message: {}", e.getMessage());
            return null;
        } catch (TimeoutException e) {
            log.error("Timeout exception during task execution, message: {}", e.getMessage());
            return null;
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
