package dev.pixelstudios.xutils.item;

import com.cryptomorin.xseries.XMaterial;
import dev.pixelstudios.xutils.item.provider.ItemProvider;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import dev.pixelstudios.xutils.objects.PlaceholderMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;
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

    public static boolean matches(ItemStack item, List<String> match) {
        return match.stream().anyMatch(value -> matches(item, value));
    }

    public static boolean matches(ItemStack item, String match) {
        String[] parts = match.split(":", 2);

        if (parts.length == 2) {
            String key = ItemProvider.lookupCustomId(item);
            return key != null && key.equalsIgnoreCase(parts[1]);
        } else {
            Optional<XMaterial> material = XMaterial.matchXMaterial(match);
            return material.isPresent() && material.get().isSimilar(item);
        }
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
