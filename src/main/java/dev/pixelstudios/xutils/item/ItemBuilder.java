package dev.pixelstudios.xutils.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import com.willfp.eco.core.items.Items;
import dev.lone.itemsadder.api.CustomStack;
import dev.pixelstudios.xutils.ReflectionUtil;
import dev.pixelstudios.xutils.VersionUtil;
import dev.pixelstudios.xutils.objects.PlaceholderMap;
import dev.pixelstudios.xutils.text.TextUtil;
import io.th0rgal.oraxen.api.OraxenItems;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
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
import java.util.UUID;

@SuppressWarnings("deprecation")
public final class ItemBuilder implements Cloneable {

    private static final Map<PotionEffectType, String> EFFECT_NAMES = new HashMap<>();

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

    public ItemBuilder(String material) {
        String[] split = material.split(":", 2);

        if (split.length == 2) {
            switch (split[0].toLowerCase()) {
                case "texture":
                    this.item = XMaterial.PLAYER_HEAD.parseItem();
                    this.meta = this.item.getItemMeta();

                    this.texture(split[1]);
                    return;
                case "itemsadder":
                case "ia":
                    this.item = CustomStack.getInstance(split[1]).getItemStack();
                    this.meta = this.item.getItemMeta();
                    return;
                case "oraxen":
                case "oxn":
                    this.item = OraxenItems.getItemById(split[1]).build();
                    this.meta = this.item.getItemMeta();
                    return;
                case "mmoitems":
                    String[] item = split[1].split(":", 2);

                    this.item = MMOItems.plugin.getItem(item[0], item[1]);
                    this.meta = this.item.getItemMeta();
                    return;
                case "ecoitems":
                    this.item = Items.lookup("ecoitems:" + split[1]).getItem();
                    this.meta = this.item.getItemMeta();

                    return;
                default:
                    throw new IllegalArgumentException("Invalid material: " + material);
            }
        } else {
            if (material.equalsIgnoreCase("SPLASH_POTION") && !XMaterial.SPLASH_POTION.isSupported()) {
                this.legacySplash = true;
                this.item = new ItemStack(Material.POTION);
            } else {
                this.item = XMaterial.matchXMaterial(material).orElse(XMaterial.STONE).parseItem();
            }
            this.meta = item.getItemMeta();
        }
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

    public ItemBuilder texture(String texture) {
        XSkull.of(meta).profile(Profileable.detect(texture)).apply();
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

    public static ItemBuilder fromConfig(ConfigurationSection section) {
        return fromConfig(section, null);
    }

    public static ItemBuilder fromConfig(ConfigurationSection section, ItemBuilder defaultItem) {
        if (!section.isString("material") && defaultItem == null) {
            throw new IllegalArgumentException("Missing material property");
        }

        ItemBuilder builder;

        if (section.isString("material")) {
            builder = new ItemBuilder(section.getString("material"));
        } else {
            builder = defaultItem.clone();
        }

        if (section.isString("name")) {
            builder.name(section.getString("name"));
        }
        if (section.isList("lore")) {
            builder.lore(section.getStringList("lore"));
        }
        if (section.getBoolean("glow")) {
            builder.glow();
        }
        if (section.isString("texture")) {
            builder.texture(section.getString("texture"));
        }
        if (section.isInt("amount")) {
            builder.amount(section.getInt("amount"));
        }
        if (section.isInt("custom-model-data")) {
            builder.customModelData(section.getInt("custom-model-data"));
        }
        if (section.getBoolean("unbreakable")) {
            builder.unbreakable();
        }
        if (section.isInt("durability")) {
            builder.durability((short) section.getInt("durability"));
        }
        if (section.isString("color")) {
            builder.color(TextUtil.parseColor(section.getString("color")));
        }
        if (section.isString("armor-trim") && ReflectionUtil.supports(20)) {
            String[] split = section.getString("armor-trim").split(",");

            if (split.length < 2) {
                throw new IllegalArgumentException("Invalid armor trim format: " + section.getString("armor-trim"));
            }

            builder.trim(new ArmorTrim(
                    Registry.TRIM_MATERIAL.match(split[0]),
                    Registry.TRIM_PATTERN.match(split[1])
            ));
        }
        if (section.isList("flags")) {
            for (String flag : section.getStringList("flags")) {
                builder.flags(ItemFlag.valueOf(flag.toUpperCase()));
            }
        }
        if (section.isList("effects")) {
            for (String effect : section.getStringList("effects")) {
                builder.effect(TextUtil.parseEffect(effect));
            }
        }
        if (section.isList("enchantments")) {
            for (String enchantment : section.getStringList("enchantments")) {
                String[] split = enchantment.split(",");

                if (split.length < 1) {
                    throw new IllegalArgumentException("Invalid enchantment format: " + enchantment);
                }

                Enchantment enchant = XEnchantment.matchXEnchantment(split[0]).orElse(XEnchantment.UNBREAKING).getEnchant();
                builder.enchant(enchant, split.length == 1 ? 1 : Integer.parseInt(split[1]));
            }
        }
        if (section.isList("modifiers")) {
            for (String modifier : section.getStringList("modifiers")) {
                String[] split = modifier.split(",");

                if (split.length < 2) {
                    throw new IllegalArgumentException("Invalid attribute modifier format: " + modifier);
                }

                builder.modifier(
                        Attribute.valueOf(split[0].toUpperCase()),
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

    @Override
    public ItemBuilder clone() {
        ItemBuilder clone = new ItemBuilder(this.item.clone());

        clone.meta = this.meta.clone();
        clone.placeholders = this.placeholders.clone();
        clone.legacySplash = this.legacySplash;

        return clone;
    }

}
