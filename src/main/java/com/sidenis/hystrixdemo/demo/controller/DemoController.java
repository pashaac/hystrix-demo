package com.sidenis.hystrixdemo.demo.controller;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
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

      @Override
      protected String getFallback() {
        return "Oops, system temporary unavailable ¯\\_(ツ)_/¯";
      }
    };

    return hystrixCommand.execute();
  }


  private HystrixCommand.Setter configuration() {
    return HystrixCommand.Setter
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("Hystrix-Demo"))
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                    .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                    .withExecutionTimeoutInMilliseconds(1000)
                    .withExecutionTimeoutEnabled(true)
                    .withExecutionIsolationThreadInterruptOnTimeout(true)
                    .withExecutionIsolationThreadInterruptOnFutureCancel(false)
                    .withExecutionIsolationSemaphoreMaxConcurrentRequests(10)
                    .withFallbackIsolationSemaphoreMaxConcurrentRequests(10)
                    .withFallbackEnabled(true)
                    .withCircuitBreakerEnabled(true)
                    .withCircuitBreakerRequestVolumeThreshold(20)
                    .withCircuitBreakerSleepWindowInMilliseconds(5000)
                    .withCircuitBreakerErrorThresholdPercentage(50)
                    .withCircuitBreakerForceOpen(false)
                    .withCircuitBreakerForceClosed(false)
                    .withMetricsRollingStatisticalWindowInMilliseconds(10000)
                    .withMetricsRollingStatisticalWindowBuckets(10)
                    .withMetricsRollingPercentileEnabled(true)
                    .withMetricsRollingPercentileWindowInMilliseconds(60000)
                    .withMetricsRollingPercentileWindowBuckets(6)
                    .withMetricsRollingPercentileBucketSize(100)
                    .withMetricsHealthSnapshotIntervalInMilliseconds(500)
                    .withRequestCacheEnabled(true)
                    .withRequestLogEnabled(true))
            .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                    .withCoreSize(10)
                    .withMaximumSize(10)
                    .withMaxQueueSize(100)
                    .withQueueSizeRejectionThreshold(5)
                    .withKeepAliveTimeMinutes(1)
                    .withAllowMaximumSizeToDivergeFromCoreSize(false)
                    .withMetricsRollingStatisticalWindowInMilliseconds(10000)
                    .withMetricsRollingStatisticalWindowBuckets(10));
  }
}
