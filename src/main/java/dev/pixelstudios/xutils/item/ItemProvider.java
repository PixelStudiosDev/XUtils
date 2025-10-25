package dev.pixelstudios.xutils.item;

import com.cryptomorin.xseries.XMaterial;
import dev.pixelstudios.xutils.config.serializer.AttributeModifierSerializer;
import dev.pixelstudios.xutils.config.serializer.ConfigSerializer;
import dev.pixelstudios.xutils.config.serializer.EnchantmentSerializer;
import dev.pixelstudios.xutils.item.provider.*;
import dev.pixelstudios.xutils.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ItemProvider {

    private static final Map<String, ItemProvider> PROVIDERS = new HashMap<>();

    static {
        register(new CustomHeadProvider());
        register(new EcoItemsProvider());
        register(new ItemsAdderProvider());
        register(new OraxenProvider());
        register(new NexoProvider());
        register(new MMOItemsProvider());
        register(new CraftEngineProvider());
    }

    public static void register(ItemProvider provider) {
        for (String id : provider.getIds()) {
            PROVIDERS.put(id, provider);
        }
    }

    public static ItemBuilder parse(String item) {
        String[] parts = item.split(":", 2);

        if (parts.length != 2) {
            Optional<XMaterial> material = XMaterial.matchXMaterial(item);

            if (!material.isPresent()) {
                TextUtil.error("Invalid material: " + item);
                return new ItemBuilder(Material.STONE);
            }

            return new ItemBuilder(material.get().parseItem());
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

    public static ItemBuilder fromConfig(ConfigurationSection section, ItemBuilder fallbackItem) {
        if (!section.isString("material") && fallbackItem == null) {
            throw new IllegalArgumentException("Missing material property");
        }

        ItemBuilder builder = section.isString("material") ?
                ItemBuilder.of(section.getString("material")) : fallbackItem.clone();

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
            builder.color(ConfigSerializer.deserialize(section.getString("color"), Color.class));
        }

        if (section.isString("armor-trim")) {
            builder.trim(ConfigSerializer.deserialize(section.getString("armor-trim"), ArmorTrim.class));
        }

        for (String flag : section.getStringList("flags")) {
            builder.flags(ItemFlag.valueOf(flag.toUpperCase()));
        }

        for (String effect : section.getStringList("effects")) {
            builder.effect(ConfigSerializer.deserialize(effect, PotionEffect.class));
        }

        for (String enchantment : section.getStringList("enchantments")) {
            EnchantmentSerializer.EnchantmentWrapper wrapper = ConfigSerializer.deserialize(enchantment, EnchantmentSerializer.EnchantmentWrapper.class);
            builder.enchant(wrapper.getEnchantment(), wrapper.getLevel());
        }

        for (String modifier : section.getStringList("modifiers")) {
            AttributeModifierSerializer.AttributeModifierWrapper wrapper = ConfigSerializer.deserialize(modifier, AttributeModifierSerializer.AttributeModifierWrapper.class);
            builder.modifier(wrapper.getAttribute(), wrapper.getModifier());
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
