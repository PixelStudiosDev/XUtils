package dev.pixelstudios.xutils.item;

import com.cryptomorin.xseries.XMaterial;
import dev.pixelstudios.xutils.ReflectionUtil;
import dev.pixelstudios.xutils.VersionUtil;
import dev.pixelstudios.xutils.text.TextUtil;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import dev.pixelstudios.xutils.text.placeholder.PlaceholderMap;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class ItemUtil {

    public static String getTag(ItemStack item, String key) {
        return TagHandler.handler().get(item, key);
    }

    public static String getTag(ItemStack item, NamespacedKey key) {
        return TagHandler.handler().get(item, key);
    }

    public static ItemStack setTag(ItemStack item, String key, String value) {
        return TagHandler.handler().set(item, key, value);
    }

    public static ItemStack setTag(ItemStack item, NamespacedKey key, String value) {
        return TagHandler.handler().set(item, key, value);
    }

    public static boolean hasTag(ItemStack item, String key) {
        String tag = getTag(item, key);
        return tag != null && !tag.isEmpty();
    }

    public static boolean hasTag(ItemStack item, NamespacedKey key) {
        String tag = getTag(item, key);
        return tag != null && !tag.isEmpty();
    }

    public static ItemStack parsePlaceholders(ItemStack item, OfflinePlayer player) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return setNameAndLore(
                    item,
                    text -> TextUtil.color(PlaceholderAPI.setPlaceholders(player, text)),
                    lines -> TextUtil.color(PlaceholderAPI.setPlaceholders(player, lines))
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
            return TextUtil.matchString(parts[1], key);
        } else {
            String material = XMaterial.matchXMaterial(item).name();
            return TextUtil.matchString(match, material);
        }
    }

    public static void give(Player player, ItemStack... items) {
        Map<Integer, ItemStack> remaining = player.getInventory().addItem(items);

        for (ItemStack item : remaining.values()) {
            player.getWorld().dropItem(player.getLocation(), item);
        }
    }

    public static void removeItemInHand(Player player, int amount, EquipmentSlot hand) {
        ItemStack item = ReflectionUtil.supports(9) ?
                (hand == EquipmentSlot.HAND ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand()) :
                player.getInventory().getItemInHand();

        if (item.getAmount() <= amount) {
            if (ReflectionUtil.supports(9)) {
                if (hand == EquipmentSlot.HAND) {
                    player.getInventory().setItemInMainHand(null);
                } else {
                    player.getInventory().setItemInOffHand(null);
                }
            } else {
                player.getInventory().setItemInHand(null);
            }
        } else {
            item.setAmount(item.getAmount() - amount);
        }
    }

    public static void removeItemInHand(Player player, int amount) {
        removeItemInHand(player, amount, EquipmentSlot.HAND);
    }

    public static ItemStack setNameAndLore(
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
