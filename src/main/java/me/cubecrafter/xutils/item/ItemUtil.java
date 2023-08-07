package me.cubecrafter.xutils.item;

import de.tr7zw.changeme.nbtapi.NBT;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.cubecrafter.xutils.ReflectionUtil;
import me.cubecrafter.xutils.XUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@UtilityClass
public class ItemUtil {

    public static String getTag(ItemStack item, String key) {
        if (ReflectionUtil.supports(14)) {
            NamespacedKey namespacedKey = new NamespacedKey(XUtils.getPlugin(), key);
            return item.getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
        } else {
            return NBT.get(item, nbt -> nbt.getString(key));
        }
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
