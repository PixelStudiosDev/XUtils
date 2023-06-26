package me.cubecrafter.xutils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class HttpUtil {

    public static CompletableFuture<Response> get(String url) {
        return get(url, new Headers());
    }

    public static CompletableFuture<Response> get(String url, Headers headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "XUtils/1.0");
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue().get(0));
                }
                return new Response(connection.getResponseCode(), read(connection.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Response.EMPTY;
        });
    }

    public static CompletableFuture<Response> post(String url, JsonObject body) {
        return post(url, body, new Headers());
    }

    public static CompletableFuture<Response> post(String url, JsonObject body, Headers headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "XUtils/1.0");
                connection.setRequestProperty("Content-Length", String.valueOf(body.toString().length()));
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue().get(0));
                }
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

        public String text() {
            return body;
        }

        public int code() {
            return code;
        }

        public JsonObject json() {
            return JsonParser.parseString(body).getAsJsonObject();
        }

        public boolean success() {
            return code >= 200 && code < 300;
        }

    }

}
