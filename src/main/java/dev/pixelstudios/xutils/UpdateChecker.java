package dev.pixelstudios.xutils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.pixelstudios.xutils.text.TextUtil;

@Getter
public class UpdateChecker {

    private final String url;
    private final String currentVersion;

    private boolean updateAvailable;
    private String latestVersion;

    public UpdateChecker(Source source, int resourceId, int checkEveryXSeconds) {
        this.url = String.format(source.url, resourceId);
        this.currentVersion = XUtils.getPlugin().getDescription().getVersion();

        Tasks.repeatAsync(this::check, 0, checkEveryXSeconds * 20L);
    }

    public void check() {
        TextUtil.info("Checking for updates...");

        HttpUtil.get(url).thenAccept(response -> {
            if (!response.success()) {
                TextUtil.warn("Failed to check for updates.");
                return;
            }

            String latest = response.text();
            if (latest.equals(currentVersion)) return;

            this.updateAvailable = true;
            this.latestVersion = latest;

            TextUtil.info("There is a new update available: " + latest);
        });
    }

    @RequiredArgsConstructor
    public enum Source {
        SPIGOT("https://api.spigotmc.org/legacy/update.php?resource=%s"),
        POLYMART("https://api.polymart.org/v1/getResourceInfoSimple/?resource_id=%s&key=version");

        private final String url;
    }

}
