package dev.pixelstudios.xutils;

import com.google.gson.JsonObject;
import lombok.Value;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class SkinUtil {

    private static final String MINESKIN_URL = "https://api.mineskin.org/generate/url";
    private static final Map<String, SkinProperties> CACHE = new ConcurrentHashMap<>();

    /**
     * Fetches the skin properties from the given texture url.
     * @param url the url of the texture
     * @return the properties of the skin
     */
    public static CompletableFuture<SkinProperties> fetchSkin(String url) {
        if (CACHE.containsKey(url)) {
            return CompletableFuture.completedFuture(CACHE.get(url));
        }

        JsonObject request = new JsonObject();
        request.addProperty("variant", "classic");
        request.addProperty("name", "");
        request.addProperty("visibility", "0");
        request.addProperty("url", url);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return HttpUtil.post(MINESKIN_URL, request, headers).thenApply(response -> {
            if (!response.success()) {
                return null;
            }
            JsonObject data = response.json().getAsJsonObject("data");
            JsonObject texture = data.getAsJsonObject("texture");

            SkinProperties properties = new SkinProperties(
                    UUID.fromString(data.get("uuid").getAsString()),
                    texture.get("value").getAsString(),
                    texture.get("signature").getAsString()
            );

            CACHE.put(url, properties);
            return properties;
        });
    }

    @Value
    public static class SkinProperties {

        UUID uuid;
        String value;
        String signature;

    }

}
