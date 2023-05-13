package me.cubecrafter.xutils;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Colorable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

@Getter
public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder(ItemStack item) {
        setItem(item);
    }

    public ItemBuilder(Material material) {
        setItem(new ItemStack(material));
    }

    public ItemBuilder(String material) {
        setItem(XMaterial.matchXMaterial(material).orElse(XMaterial.STONE).parseItem());
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(TextUtil.color(name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(TextUtil.color(lore));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        Damageable meta = (Damageable) item.getItemMeta();
        meta.setDamage(durability);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDyeColor(DyeColor color) {
        Colorable colorable = (Colorable) item.getData();
        colorable.setColor(color);
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        if (glow) {
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addPotionEffect(PotionEffectType type, int duration, int amplifier) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setPotionColor(Color color) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(unbreakable);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchantment, level, true);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(itemFlags);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setSkullTexture(String texture) {
        ItemMeta meta = item.getItemMeta();
        SkullUtils.applySkin(meta, texture);
        item.setItemMeta(meta);
        return this;
    }

    public <T, Z> ItemBuilder setPersistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, type, value);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return item;
    }

    public static boolean hasPersistentData(ItemStack item, NamespacedKey key) {
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }

    public static <T, Z> Z getPersistentData(ItemStack item, NamespacedKey key, PersistentDataType<T, Z> type) {
        return item.getItemMeta().getPersistentDataContainer().get(key, type);
    }

}
