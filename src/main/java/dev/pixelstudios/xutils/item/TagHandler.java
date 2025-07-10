package dev.pixelstudios.xutils.item;

import de.tr7zw.changeme.nbtapi.NBT;
import dev.pixelstudios.xutils.ReflectionUtil;
import dev.pixelstudios.xutils.XUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public interface TagHandler {

    ItemStack set(ItemStack item, String key, String value);
    ItemStack set(ItemStack item, NamespacedKey key, String value);

    String get(ItemStack item, String key);
    String get(ItemStack item, NamespacedKey key);

    static TagHandler modern() {
        return new TagHandler() {

            @Override
            public ItemStack set(ItemStack item, String key, String value) {
                NamespacedKey namespacedKey = new NamespacedKey(XUtils.getPlugin(), key);
                return set(item, namespacedKey, value);
            }

            @Override
            public ItemStack set(ItemStack item, NamespacedKey key, String value) {
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
                item.setItemMeta(meta);

                return item;
            }

            @Override
            public String get(ItemStack item, String key) {
                NamespacedKey namespacedKey = new NamespacedKey(XUtils.getPlugin(), key);
                return get(item, namespacedKey);
            }

            @Override
            public String get(ItemStack item, NamespacedKey key) {
                return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            }

        };
    }

    static TagHandler legacy() {
        return new TagHandler() {

            @Override
            public ItemStack set(ItemStack item, String key, String value) {
                NBT.modify(item, nbt -> {
                    nbt.setString(key, value);
                });
                return item;
            }

            @Override
            public String get(ItemStack item, String key) {
                return NBT.get(item, nbt -> {
                    return nbt.getString(key);
                });
            }

            @Override
            public ItemStack set(ItemStack item, NamespacedKey key, String value) {
                throw new UnsupportedOperationException("Legacy tag handler does not support NamespacedKey");
            }

            @Override
            public String get(ItemStack item, NamespacedKey key) {
                throw new UnsupportedOperationException("Legacy tag handler does not support NamespacedKey");
            }

        };
    }

    static TagHandler handler() {
        if (XUtils.getCustomTagHandler() != null) {
            return XUtils.getCustomTagHandler();
        } else {
            return ReflectionUtil.supports(14) ? modern() : legacy();
        }
    }

}
