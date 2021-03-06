package com.sidenis.hystrixdemo.sample_01;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Based on clean Java native client
 * Link: https://stackoverflow.com/a/5445161
 */
public class Client {

  private static final Logger log = Logger.getLogger(Client.class.getName());

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
    call().ifPresent(log::info);
  }

}















