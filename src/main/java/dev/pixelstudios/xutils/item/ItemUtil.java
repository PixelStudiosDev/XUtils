package dev.pixelstudios.xutils.item;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import dev.pixelstudios.xutils.objects.PlaceholderMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Function;

@UtilityClass
public class ItemUtil {

    public static String getTag(ItemStack item, String key) {
        return TagHandler.handler().get(item, key);
    }

    public static ItemStack setTag(ItemStack item, String key, String value) {
        return TagHandler.handler().set(item, key, value);
    }

    public static boolean hasTag(ItemStack item, String key) {
        String tag = getTag(item, key);
        return tag != null && !tag.isEmpty();
    }

    public static ItemStack parsePlaceholders(ItemStack item, OfflinePlayer player) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return setNameAndLore(
                    item,
                    text -> PlaceholderAPI.setPlaceholders(player, text),
                    lines -> PlaceholderAPI.setPlaceholders(player, lines)
            );
        }
        return item;
    }

    public static ItemStack parsePlaceholders(ItemStack item, PlaceholderMap placeholders) {
        return setNameAndLore(item, placeholders::parse, placeholders::parse);
    }

    private static ItemStack setNameAndLore(
            ItemStack item,
            Function<String, String> name,
            Function<List<String>, List<String>> lore
    ) {
        ItemMeta meta = item.getItemMeta();

        if (meta.hasDisplayName()) {
            meta.setDisplayName(name.apply(meta.getDisplayName()));
        }
        if (meta.hasLore()) {
            meta.setLore(lore.apply(meta.getLore()));
        }

        item.setItemMeta(meta);
        return item;
    }

}
