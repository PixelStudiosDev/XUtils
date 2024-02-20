package dev.pixelstudios.xutils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class HttpUtil {

    /**
     * Performs a GET request to the given URL.
     * @param url the request URL
     * @return a response object
     */
    public static CompletableFuture<Response> get(String url) {
        return get(url, Collections.emptyMap());
    }


    /**
     * Performs a GET request to the given URL.
     * @param url the request URL
     * @param headers the request headers
     * @return a response object
     */
    public static CompletableFuture<Response> get(String url, Map<String, String> headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "XUtils/1.0");

                headers.forEach(connection::setRequestProperty);

                return new Response(connection.getResponseCode(), read(connection.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Response.EMPTY;
        });
    }

    /**
     * Performs a POST request to the given URL.
     * @param url the request URL
     * @param body the request body
     * @return a response object
     */
    public static CompletableFuture<Response> post(String url, JsonObject body) {
        return post(url, body, Collections.emptyMap());
    }


    /**
     * Performs a POST request to the given URL.
     * @param url the request URL
     * @param body the request body
     * @param headers the request headers
     * @return a response object
     */
    public static CompletableFuture<Response> post(String url, JsonObject body, Map<String, String> headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "XUtils/1.0");
                connection.setRequestProperty("Content-Length", String.valueOf(body.toString().length()));

                headers.forEach(connection::setRequestProperty);

                connection.setDoOutput(true);
                connection.getOutputStream().write(body.toString().getBytes());

                return new Response(connection.getResponseCode(), read(connection.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Response.EMPTY;
        });
    }

    private static String read(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    @RequiredArgsConstructor
    public static class Response {

        public static final Response EMPTY = new Response(404, "");

        private final int code;
        private final String body;

        /**
         * Returns the response body as a string.
         * @return the response body
         */
        public String text() {
            return body;
        }

        /**
         * Returns the response code.
         * @return the response code
         */
        public int code() {
            return code;
        }

        /**
         * Returns the response body as a JsonObject.
         * @return the response body
         */
        public JsonObject json() {
            return JsonParser.parseString(body).getAsJsonObject();
        }

        /**
         * Returns whether the request was successful.
         * @return true if the request was successful, false otherwise
         */
        public boolean success() {
            return code >= 200 && code < 300;
        }

    }

}
