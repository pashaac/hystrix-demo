package com.sidenis.hystrixdemo.sample_02_1;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Based on clean Java native client with thread pool and timeouts on outside calls
 */
public class Client {

  private static final Logger log = Logger.getLogger(Client.class.getName());
  private static final ExecutorService service = Executors.newFixedThreadPool(3);

  @SuppressWarnings("Duplicates")
  private static Optional<String> call() {
    String url = "https://jsonplaceholder.typicode.com/users/1";
    try {
      try (InputStream stream = new URL(url).openStream(); Reader reader = new InputStreamReader(stream)) {
        return Optional.of(toString(reader).replaceAll("\\s+", " "));
      }
    } catch (IOException e) {
      log.severe("Error during network call to URL: " + url + ", message: " + e.getMessage());
      return Optional.empty();
    }
  }

  @SuppressWarnings("All")
  private static String toString(Reader reader) throws IOException {
    return CharStreams.toString(reader);
  }

  public static void main(String[] args) {
    Stream.of(callAsync(), callAsync(), callAsync())
            .map(Client::get)
            .forEach(result -> result.ifPresent(log::info));
    service.shutdown();
  }

  private static Future<Optional<String>> callAsync() {
    return service.submit(Client::call);
  }

  private static Optional<String> get(Future<Optional<String>> future) {
    try {
      return future.get(1, TimeUnit.SECONDS);
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
}












