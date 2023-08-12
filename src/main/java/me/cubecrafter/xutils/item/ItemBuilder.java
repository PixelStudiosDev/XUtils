package me.cubecrafter.xutils.item;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import lombok.Setter;
import me.cubecrafter.xutils.ReflectionUtil;
import me.cubecrafter.xutils.VersionUtil;
import me.cubecrafter.xutils.text.TextUtil;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Colorable;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("deprecation")
public final class ItemBuilder {

    private final ItemStack item;
    private ItemMeta meta;

    private final Map<String, String> placeholders = new HashMap<>();

    private boolean legacySplash;

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(String material) {
        if (material.equalsIgnoreCase("SPLASH_POTION") && !XMaterial.SPLASH_POTION.isSupported()) {
            this.legacySplash = true;
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

    public ItemBuilder setArmorColor(Color color) {
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
        } else {
            meta.removeEnchant(Enchantment.DURABILITY);
        }
        return this;
    }

    public ItemBuilder addPotionEffect(PotionEffect effect) {
        PotionType potionType = PotionType.getByEffect(effect.getType());

        if (legacySplash) {
            new Potion(potionType, effect.getAmplifier() + 1, true, effect.getDuration() > 200).apply(item);
        } else {
            PotionMeta meta = (PotionMeta) this.meta;
            meta.addCustomEffect(effect, true);

            if (ReflectionUtil.supports(9)) {
                if (potionType != null) {
                    meta.setBasePotionData(new PotionData(potionType));
                }
            } else {
                meta.setMainEffect(effect.getType());
            }
        }
        return this;
    }

    public ItemBuilder setPotionColor(Color color) {
        if (!ReflectionUtil.supports(11)) {
            return this;
        }
        ((PotionMeta) meta).setColor(color);
        return this;
    }

    public ItemBuilder setCustomModelData(int modelData) {
        if (!ReflectionUtil.supports(14)) {
            return this;
        }
        meta.setCustomModelData(modelData);
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
        item.setItemMeta(meta);
        TagHandler.handler().set(item, key, value);
        meta = item.getItemMeta();
        return this;
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        if (!ReflectionUtil.supports(13)) {
            return this;
        }
        meta.addAttributeModifier(attribute, modifier);
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
        if (section.contains("custom-model-data")) {
            builder.setCustomModelData(section.getInt("custom-model-data"));
        }
        if (section.contains("unbreakable")) {
            builder.setUnbreakable(section.getBoolean("unbreakable"));
        }
        if (section.contains("durability")) {
            builder.setDurability((short) section.getInt("durability"));
        }
        if (section.contains("dye-color")) {
            builder.setDyeColor(DyeColor.valueOf(section.getString("dye-color").toUpperCase()));
        }
        if (section.contains("armor-color")) {
            builder.setArmorColor(TextUtil.parseColor(section.getString("armor-color")));
        }
        if (section.contains("potion-color")) {
            builder.setPotionColor(TextUtil.parseColor(section.getString("potion-color")));
        }
        if (section.contains("flags")) {
            for (String flag : section.getStringList("flags")) {
                builder.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
            }
        }
        if (section.contains("effects")) {
            for (String effect : section.getStringList("effects")) {
                builder.addPotionEffect(TextUtil.parseEffect(effect));
            }
        }
        if (section.contains("enchantments")) {
            for (String enchantment : section.getStringList("enchantments")) {
                String[] split = enchantment.replace(" ", "").split(",");
                if (split.length < 1) {
                    throw new IllegalArgumentException("Invalid enchantment format: " + enchantment);
                }

                Enchantment enchant = XEnchantment.matchXEnchantment(split[0]).orElse(XEnchantment.DURABILITY).getEnchant();
                builder.addEnchant(enchant, split.length == 1 ? 1 : Integer.parseInt(split[1]));
            }
        }
        if (section.contains("modifiers")) {
            for (String modifier : section.getStringList("modifiers")) {
                String[] split = modifier.replace(" ", "").split(",");
                if (split.length < 2) {
                    throw new IllegalArgumentException("Invalid attribute modifier format: " + modifier);
                }

                builder.addAttributeModifier(Attribute.valueOf(split[0].toUpperCase()),
                        new AttributeModifier(
                            UUID.randomUUID(),
                            "custom_modifier",
                            Double.parseDouble(split[1]),
                            AttributeModifier.Operation.ADD_NUMBER,
                            split.length == 3 ? EquipmentSlot.valueOf(split[2].toUpperCase()) : null
                        )
                );
            }
        }

        return builder;
    }

}
