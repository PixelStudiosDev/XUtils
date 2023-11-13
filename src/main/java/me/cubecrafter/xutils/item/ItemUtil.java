package me.cubecrafter.xutils.item;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public static ItemStack parsePlaceholders(OfflinePlayer player, ItemStack item) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            ItemMeta meta = item.getItemMeta();

            if (meta.hasDisplayName()) {
                meta.setDisplayName(PlaceholderAPI.setPlaceholders(player, meta.getDisplayName()));
            }
            if (meta.hasLore()) {
                meta.setLore(PlaceholderAPI.setPlaceholders(player, meta.getLore()));
            }

            item.setItemMeta(meta);
        }
        return item;
    }

}
