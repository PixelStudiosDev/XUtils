package me.cubecrafter.xutils;

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
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class HttpUtil {

    public static CompletableFuture<Response> get(String url, String... headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                for (int i = 0; i < headers.length; i += 2) {
                    connection.setRequestProperty(headers[i], headers[i + 1]);
                }
                return new Response(connection.getResponseCode(), read(connection.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Response.EMPTY;
        });
    }

    public static CompletableFuture<Response> post(String url, String body, String... headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                for (int i = 0; i < headers.length; i += 2) {
                    connection.setRequestProperty(headers[i], headers[i + 1]);
                }
                connection.setRequestProperty("Content-Length", String.valueOf(body.length()));
                connection.setDoOutput(true);
                connection.getOutputStream().write(body.getBytes());
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
