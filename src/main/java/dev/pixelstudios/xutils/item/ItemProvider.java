package dev.pixelstudios.xutils.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import dev.pixelstudios.xutils.ReflectionUtil;
import dev.pixelstudios.xutils.item.provider.CustomHeadProvider;
import dev.pixelstudios.xutils.item.provider.EcoItemsProvider;
import dev.pixelstudios.xutils.item.provider.ItemsAdderProvider;
import dev.pixelstudios.xutils.item.provider.MMOItemsProvider;
import dev.pixelstudios.xutils.item.provider.NexoProvider;
import dev.pixelstudios.xutils.item.provider.OraxenProvider;
import dev.pixelstudios.xutils.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class ItemProvider {

    private static final Map<String, ItemProvider> PROVIDERS = new HashMap<>();

    static {
        register(new CustomHeadProvider());
        register(new EcoItemsProvider());
        register(new ItemsAdderProvider());
        register(new OraxenProvider());
        register(new NexoProvider());
        register(new MMOItemsProvider());
    }

    public static void register(ItemProvider provider) {
        for (String id : provider.getIds()) {
            PROVIDERS.put(id, provider);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static ItemBuilder parse(String item) {
        String[] parts = item.split(":", 2);

        if (parts.length != 2) {
            if (item.equalsIgnoreCase("SPLASH_POTION") && !XMaterial.SPLASH_POTION.isSupported()) {
                return new ItemBuilder(XMaterial.POTION.parseItem()).legacySplash();
            } else {
                Optional<XMaterial> material = XMaterial.matchXMaterial(item);

                if (!material.isPresent()) {
                    TextUtil.error("Invalid material: " + item);
                    return new ItemBuilder(Material.STONE);
                }

                return new ItemBuilder(material.get().parseItem());
            }
        }

        ItemBuilder builder = getItem(parts[0].toLowerCase(), parts[1]);

        if (builder == null) {
            TextUtil.error("Invalid item: " + item);
            return new ItemBuilder(Material.STONE);
        }

        return builder;
    }

    public static String lookupCustomId(ItemStack item) {
        for (ItemProvider provider : PROVIDERS.values()) {
            if (!provider.isEnabled()) continue;

            String id = provider.getItemKey(item);
            if (id != null) return id;
        }
        return null;
    }

    private static ItemBuilder getItem(String key, String item) {
        ItemProvider provider = PROVIDERS.get(key);
        if (provider == null || !provider.isEnabled()) return null;

        ItemStack stack = provider.getItem(item);
        if (stack == null) return null;

        return new ItemBuilder(stack);
    }

    public static ItemBuilder fromConfig(ConfigurationSection section, ItemBuilder defaultItem) {
        return fromConfig(section, defaultItem, null);
    }

    public static ItemBuilder fromConfig(ConfigurationSection section, ItemBuilder defaultItem, Player viewer) {
        if (!section.isString("material") && defaultItem == null) {
            throw new IllegalArgumentException("Missing material property");
        }

        ItemBuilder builder;

        if (section.isString("material")) {
            builder = ItemBuilder.of(section.getString("material"));
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
            builder.texture(section.getString("texture"), viewer);
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
            String[] split = section.getString("armor-trim").split(":");

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
                String[] split = enchantment.split(":");

                if (split.length < 1) {
                    throw new IllegalArgumentException("Invalid enchantment format: " + enchantment);
                }

                Enchantment enchant = XEnchantment.matchXEnchantment(split[0]).orElse(XEnchantment.UNBREAKING).getEnchant();
                builder.enchant(enchant, split.length == 1 ? 1 : Integer.parseInt(split[1]));
            }
        }

        if (section.isList("modifiers")) {
            for (String modifier : section.getStringList("modifiers")) {
                String[] split = modifier.split(":");

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

    public boolean isEnabled() {
        return getPlugin() == null || Bukkit.getPluginManager().isPluginEnabled(getPlugin());
    }

    public abstract String[] getIds();
    public abstract String getPlugin();
    public abstract ItemStack getItem(String item);
    public abstract String getItemKey(ItemStack item);

}
