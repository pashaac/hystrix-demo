package com.sidenis.hystrixdemo.sample_02;

import com.google.common.io.CharStreams;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Optional;
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

    private static Optional<String> call(Future<String> future) {
        try {
            return Optional.ofNullable(future.get(3, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            log.severe("Task execution was interrupted, message: " + e.getMessage());
            return Optional.empty();
        } catch (ExecutionException e) {
            log.severe("Execution exception during task execution, message: " + e.getMessage());
            return Optional.empty();
        } catch (TimeoutException e) {
            log.severe("Timeout exception during task execution, message: " + e.getMessage());
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        String url = "https://jsonplaceholder.typicode.com/users/1";
        Future<String> future1 = call(url);
        Future<String> future2 = call(url);
        Future<String> future3 = call(url);

        call(future1).ifPresent(log::info);
        call(future2).ifPresent(log::info);
        call(future3).ifPresent(log::info);

        service.shutdown();
    }


}












