package dev.pixelstudios.xutils.text.placeholder.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PAPIExpansion extends PlaceholderExpansion {

    private final Plugin plugin;
    private final String identifier;

    private final List<PAPIPlaceholder> placeholders = new ArrayList<>();

    private PAPIExpansion(Plugin plugin, String identifier) {
        this.plugin = plugin;
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        for (PAPIPlaceholder placeholder : placeholders) {
            String result = placeholder.parse(player, params);

            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public PAPIExpansion addPlaceholders(PAPIPlaceholder... placeholders) {
        this.placeholders.addAll(Arrays.asList(placeholders));
        this.placeholders.sort((a, b) -> b.getId().length() - a.getId().length());
        return this;
    }

    public static PAPIExpansion register(Plugin plugin, String identifier) {
        PAPIExpansion expansion = new PAPIExpansion(plugin, identifier);
        expansion.register();

        return expansion;
    }

}
