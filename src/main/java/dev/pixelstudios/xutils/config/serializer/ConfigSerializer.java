package dev.pixelstudios.xutils.config.serializer;

import dev.pixelstudios.xutils.ReflectionUtil;
import dev.pixelstudios.xutils.config.Configuration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public final class ConfigSerializer {

    private static final Map<Class<?>, Serializer<?>> SERIALIZERS = new HashMap<>();

    static {
        register(Location.class, new LocationSerializer());
        register(Color.class, new ColorSerializer());
        register(PotionEffect.class, new PotionEffectSerializer());
        register(EnchantmentSerializer.EnchantmentWrapper.class, new EnchantmentSerializer());

        register(ArmorTrim.class, ArmorTrimSerializer::new, 20);
        register(AttributeModifierSerializer.AttributeModifierWrapper.class, AttributeModifierSerializer::new, 9);
    }

    public static <T> void register(Class<T> clazz, Serializer<T> serializer) {
        SERIALIZERS.put(clazz, serializer);
    }

    public static <T> void register(Class<T> clazz, Supplier<Serializer<T>> serializer, int requiredVersion) {
        if (ReflectionUtil.supports(requiredVersion)) {
            register(clazz, serializer.get());
        }
    }

    public static <T> String serialize(T object) {
        return ((Serializer<T>) getSerializer(object.getClass())).serialize(object);
    }

    public static <T> void serialize(T object, Configuration config, String path) {
        ((Serializer<T>) getSerializer(object.getClass())).serialize(object, config, path);
    }

    public static <T> T deserialize(String str, Class<T> objectClass) {
        return getSerializer(objectClass).deserialize(str);
    }

    public static <T> T deserialize(Class<T> objectClass, Configuration config, String path) {
        return getSerializer(objectClass).deserialize(config, path);
    }

    private static <T> Serializer<T> getSerializer(Class<T> objectClass) {
        Serializer<T> serializer = (Serializer<T>) SERIALIZERS.get(objectClass);

        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for " + objectClass);
        }

        return serializer;
    }

}
