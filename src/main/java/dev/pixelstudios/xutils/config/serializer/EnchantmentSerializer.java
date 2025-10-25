package dev.pixelstudios.xutils.config.serializer;

import com.cryptomorin.xseries.XEnchantment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentSerializer implements Serializer<EnchantmentSerializer.EnchantmentWrapper> {

    @Override
    public EnchantmentWrapper deserialize(String serialized) {
        String[] split = serialized.split(":");

        if (split.length < 1) {
            throw new IllegalArgumentException("Invalid enchantment format: " + serialized);
        }

        Enchantment enchantment = XEnchantment.of(split[0]).orElse(XEnchantment.UNBREAKING).get();

        return new EnchantmentWrapper(
                enchantment,
                split.length == 1 ? 1 : Integer.parseInt(split[1])
        );
    }

    @Getter
    @RequiredArgsConstructor
    public static class EnchantmentWrapper {

        private final Enchantment enchantment;
        private final int level;

    }

}
