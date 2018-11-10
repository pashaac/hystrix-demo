package com.sidenis.hystrixdemo.sample_02_2;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Random;
import java.util.concurrent.*;

@Component
public class Client {

  private static final Logger log = LoggerFactory.getLogger(Client.class);

  private static final int TIMEOUT = 500;

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

  @SuppressWarnings("UnusedReturnValue")
  private Future<String> callAsync() {
    return service.submit(() -> {
      Thread.sleep(Math.round(100 + 400 * random.nextDouble()));
      return restTemplate.getForObject("https://jsonplaceholder.typicode.com/users/1", String.class);
    });
  }

  @Scheduled(fixedDelay = 100) // 1 task <-> 1 thread <-> 300ms
  void load() {
    callAsync();
  }

  @Scheduled(fixedDelay = 1_000)
  void queueChecker() {
    log.info("Queue size: {}", queue.size());
  }

}
