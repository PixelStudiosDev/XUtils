package dev.pixelstudios.xutils.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import dev.pixelstudios.xutils.ReflectionUtil;
import dev.pixelstudios.xutils.VersionUtil;
import dev.pixelstudios.xutils.placeholder.PlaceholderMap;
import dev.pixelstudios.xutils.text.TextUtil;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.material.Colorable;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public final class ItemBuilder implements Cloneable {

    private static final Map<PotionEffectType, String> EFFECT_NAMES = new HashMap<>();
    private static final Profileable FALLBACK_PROFILE = Profileable.username("MHF_Steve");

    static {
        EFFECT_NAMES.put(XPotion.INSTANT_DAMAGE.getPotionEffectType(), "Harming");
        EFFECT_NAMES.put(XPotion.INSTANT_HEALTH.getPotionEffectType(), "Healing");
        EFFECT_NAMES.put(XPotion.SPEED.getPotionEffectType(), "Swiftness");
        EFFECT_NAMES.put(XPotion.SLOWNESS.getPotionEffectType(), "Slowness");
        EFFECT_NAMES.put(XPotion.JUMP_BOOST.getPotionEffectType(), "Leaping");
        EFFECT_NAMES.put(XPotion.STRENGTH.getPotionEffectType(), "Strength");
    }

    private ItemStack item;
    private ItemMeta meta;

    private PlaceholderMap placeholders = new PlaceholderMap();

    private boolean legacySplash;

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public static ItemBuilder of(String item) {
        return ItemProvider.parse(item);
    }

    public static ItemBuilder fromConfig(ConfigurationSection section) {
        return ItemProvider.fromConfig(section, null);
    }

    public static ItemBuilder fromConfig(ConfigurationSection section, ItemBuilder defaultItem) {
        return ItemProvider.fromConfig(section, defaultItem);
    }

    public static ItemBuilder fromConfig(ConfigurationSection section, ItemBuilder defaultItem, Player viewer) {
        return ItemProvider.fromConfig(section, defaultItem, viewer);
    }

    public ItemBuilder legacySplash() {
        this.legacySplash = true;
        return this;
    }

    public ItemBuilder name(String name) {
        meta.setDisplayName(TextUtil.color(name));
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        meta.setLore(TextUtil.color(lore));
        return this;
    }

    public ItemBuilder lore(String... lore) {
        lore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLore(List<String> lines) {
        List<String> lore = meta.getLore();
        lore.addAll(TextUtil.color(lines));
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder addLore(String... lines) {
        addLore(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder durability(short durability) {
        if (ReflectionUtil.supports(13)) {
            ((Damageable) meta).setDamage(durability);
        } else {
            item.setDurability(durability);
        }
        return this;
    }

    public ItemBuilder color(Color color) {
        if (item.getData() instanceof Colorable) {
            ((Colorable) item.getData()).setColor(DyeColor.getByColor(color));

        } else if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(color);

        } else if (meta instanceof FireworkMeta) {
            ((FireworkMeta) meta).addEffect(FireworkEffect.builder().withColor(color).build());

        } else if (meta instanceof PotionMeta) {
            if (ReflectionUtil.supports(11)) {
                ((PotionMeta) meta).setColor(color);
            }
        }
        return this;
    }

    public ItemBuilder trim(ArmorTrim trim) {
        if ((meta instanceof ArmorMeta) && ReflectionUtil.supports(20)) {
            ((ArmorMeta) meta).setTrim(trim);
        }
        return this;
    }

    public ItemBuilder effect(PotionEffect effect) {
        if (legacySplash) {
            PotionType type = PotionType.getByEffect(effect.getType());
            new Potion(type, effect.getAmplifier() + 1, true, effect.getDuration() > 200).apply(item);
        } else {
            ((PotionMeta) meta).addCustomEffect(effect, true);

            if (ReflectionUtil.supports(11)) {
                color(effect.getType().getColor());
            }
            if (!meta.hasDisplayName()) {
                String material = TextUtil.formatEnumName(item.getType().toString());
                String potion = EFFECT_NAMES.getOrDefault(effect.getType(), TextUtil.formatEnumName(effect.getType().getName()));
                name("&f" + material + " of " + potion);
            }
        }
        return this;
    }

    public ItemBuilder customModelData(int modelData) {
        if (ReflectionUtil.supports(14)) {
            meta.setCustomModelData(modelData);
        }
        return this;
    }

    public ItemBuilder unbreakable() {
        VersionUtil.setUnbreakable(meta, true);
        return this;
    }

    public ItemBuilder glow() {
        enchant(XEnchantment.UNBREAKING.getEnchant(), 1);
        flags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder flags(ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder texture(String texture, Player viewer) {
        if (viewer != null && texture.equals("{player}")) {
            texture = viewer.getName();
        }

        XSkull.of(meta).profile(Profileable.detect(texture))
                .fallback(FALLBACK_PROFILE)
                .apply();

        return this;
    }

    public ItemBuilder modifier(Attribute attribute, AttributeModifier modifier) {
        if (ReflectionUtil.supports(13)) {
            meta.addAttributeModifier(attribute, modifier);
        }
        return this;
    }

    public ItemBuilder placeholder(String target, String replacement) {
        placeholders.add(target, replacement);
        return this;
    }

    public ItemBuilder placeholders(PlaceholderMap placeholders) {
        this.placeholders = placeholders;
        return this;
    }

    public ItemBuilder tag(String key, String value) {
        this.item.setItemMeta(meta);
        this.item = ItemUtil.setTag(item, key, value);
        this.meta = item.getItemMeta();
        return this;
    }

    public ItemBuilder tag(NamespacedKey key, String value) {
        this.item.setItemMeta(meta);
        this.item = ItemUtil.setTag(item, key, value);
        this.meta = item.getItemMeta();
        return this;
    }

    public ItemStack build() {
        if (meta.hasDisplayName()) {
            name(placeholders.parse(meta.getDisplayName()));
        }
        if (meta.hasLore()) {
            lore(placeholders.parse(meta.getLore()));
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ItemBuilder clone() {
        ItemBuilder clone = new ItemBuilder(this.item.clone());

        clone.meta = this.meta.clone();
        clone.placeholders = this.placeholders.clone();
        clone.legacySplash = this.legacySplash;

        return clone;
    }

}
