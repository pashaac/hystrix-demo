package com.sidenis.hystrixdemo.sample_02;

import com.google.common.io.CharStreams;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Based on clean Java native client with thread pool and timeouts on outside calls
 */
public class Client {

    private static final Logger log = Logger.getLogger(Client.class.getName());
    private static final ExecutorService service = Executors.newFixedThreadPool(3);

    private static Future<String> call(String url) {
        return service.submit(() -> {
            try (InputStream stream = new URL(url).openStream(); Reader reader = new InputStreamReader(stream)) {
                return CharStreams.toString(reader);
            }
        });
    }

    @Nullable
    private static String call(Future<String> future) {
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.severe("Task execution was interrupted, message: " + e.getMessage());
            return null;
        } catch (ExecutionException e) {
            log.severe("Execution exception during task execution, message: " + e.getMessage());
            return null;
        } catch (TimeoutException e) {
            log.severe("Timeout exception during task execution, message: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        String url = "https://jsonplaceholder.typicode.com/users";
        Future<String> future1 = call(url);
        Future<String> future2 = call(url);
        Future<String> future3 = call(url);

        log.info(call(future1));
        log.info(call(future2));
        log.info(call(future3));
        service.shutdown();
    }


}












