package com.sidenis.hystrixdemo.sample_01;

import com.google.common.io.CharStreams;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Based on clean Java native client
 * Link: https://stackoverflow.com/a/5445161
 */
public class Client {

    private static final Logger log = Logger.getLogger(Client.class.getName());

    @Nullable
    private static String call(String url) {
        try {
            try (InputStream stream = new URL(url).openStream(); Reader reader = new InputStreamReader(stream)) {
                return CharStreams.toString(reader);
            }
//            try (InputStream stream = new URL(url).openStream(); Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name())) {
//                return scanner.useDelimiter("\\A").next();
//            }
        } catch (IOException e) {
            log.severe("Error during network call to URL: " + url + ", message: " + e.getMessage());
            return null;
        }
    }


    public static void main(String[] args) {
        log.info(call("https://jsonplaceholder.typicode.com/users"));
    }

}















