package me.cubecrafter.xutils;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XItemStack;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBT;
import me.cubecrafter.xutils.text.TextUtil;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Colorable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ItemBuilder {

    private final ItemStack item;
    private ItemMeta meta;

    private final Map<String, String> placeholders = new HashMap<>();
    private boolean legacyPotion;

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(String material) {
        if (material.equalsIgnoreCase("SPLASH_POTION") && !XMaterial.SPLASH_POTION.isSupported()) {
            this.legacyPotion = true;
            this.item = new ItemStack(Material.POTION);
        } else {
            this.item = XMaterial.matchXMaterial(material).orElse(XMaterial.STONE).parseItem();
        }
        this.meta = item.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(TextUtil.color(name));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(TextUtil.color(lore));
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
        if (ReflectionUtil.supports(13)) {
            ((Damageable) meta).setDamage(durability);
        } else {
            item.setDurability(durability);
        }
        return this;
    }

    public ItemBuilder setColor(Color color) {
        ((LeatherArmorMeta) meta).setColor(color);
        return this;
    }

    public ItemBuilder setDyeColor(DyeColor color) {
        ((Colorable) item.getData()).setColor(color);
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        if (glow) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder addPotionEffect(PotionEffect effect) {
        PotionType potionType = PotionType.getByEffect(effect.getType());

        if (legacyPotion) {
            new Potion(potionType, effect.getAmplifier() + 1, true, effect.getDuration() > 200).apply(item);
        } else {
            PotionMeta meta = (PotionMeta) this.meta;
            meta.addCustomEffect(effect, true);
            if (potionType != null) {
                meta.setBasePotionData(new PotionData(potionType));
            }
        }
        return this;
    }

    public ItemBuilder setPotionColor(Color color) {
        ((PotionMeta) meta).setColor(color);
        return this;
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        meta.setCustomModelData(customModelData);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        VersionUtil.setUnbreakable(meta, unbreakable);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder setSkullTexture(String texture) {
        SkullUtils.applySkin(meta, texture);
        return this;
    }

    public ItemBuilder setTag(String key, String value) {
        if (ReflectionUtil.supports(14)) {
            NamespacedKey namespacedKey = new NamespacedKey(XUtils.getPlugin(), key);
            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
        } else {
            item.setItemMeta(meta);
            NBT.modify(item, nbt -> {
                nbt.setString(key, value);
            });
            meta = item.getItemMeta();
        }
        return this;
    }

    public ItemBuilder addPlaceholder(String target, String replacement) {
        placeholders.put(target, replacement);
        return this;
    }

    public ItemStack build() {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String name = meta.getDisplayName();
            setDisplayName(name.replace(entry.getKey(), entry.getValue()));

            if (!meta.hasLore()) continue;

            List<String> lore = meta.getLore();
            lore.replaceAll(line -> line.replace(entry.getKey(), entry.getValue()));
            setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static String getTag(ItemStack item, String key) {
        if (ReflectionUtil.supports(14)) {
            NamespacedKey namespacedKey = new NamespacedKey(XUtils.getPlugin(), key);
            return item.getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
        } else {
            return NBT.get(item, nbt -> nbt.getString(key));
        }
    }

    public static ItemBuilder fromConfig(ConfigurationSection section) {
        if (!section.contains("material")) {
            throw new IllegalArgumentException("Missing material property");
        }
        ItemBuilder builder = new ItemBuilder(section.getString("material"));

        if (section.contains("name")) {
            builder.setDisplayName(section.getString("name"));
        }
        if (section.contains("lore")) {
            builder.setLore(section.getStringList("lore"));
        }
        if (section.contains("glow")) {
            builder.setGlow(section.getBoolean("glow"));
        }
        if (section.contains("texture")) {
            builder.setSkullTexture(section.getString("texture"));
        }
        if (section.contains("amount")) {
            builder.setAmount(section.getInt("amount"));
        }
        if (section.contains("effect")) {
            builder.addPotionEffect(TextUtil.parseEffect(section.getString("effect")));
        }
        if (section.contains("custom-model-data")) {
            builder.setCustomModelData(section.getInt("custom-model-data"));
        }
        if (section.contains("unbreakable")) {
            builder.setUnbreakable(section.getBoolean("unbreakable"));
        }
        if (section.contains("dye-color")) {
            builder.setDyeColor(DyeColor.valueOf(section.getString("dye-color").toUpperCase()));
        }
        if (section.contains("durability")) {
            builder.setDurability((short) section.getInt("durability"));
        }
        if (section.contains("armor-color")) {
            builder.setColor(TextUtil.parseColor(section.getString("armor-color")));
        }
        if (section.contains("potion-color")) {
            builder.setPotionColor(TextUtil.parseColor(section.getString("potion-color")));
        }
        if (section.contains("flags")) {
            for (String flag : section.getStringList("flags")) {
                builder.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
            }
        }
        if (section.contains("enchantments")) {
            for (String enchantment : section.getStringList("enchantments")) {
                String[] split = enchantment.split(",");
                Enchantment enchant = XEnchantment.matchXEnchantment(split[0]).orElse(XEnchantment.DURABILITY).getEnchant();
                builder.addEnchant(enchant, split.length < 2 ? 1 : Integer.parseInt(split[1]));
            }
        }

        return builder;
    }

}
