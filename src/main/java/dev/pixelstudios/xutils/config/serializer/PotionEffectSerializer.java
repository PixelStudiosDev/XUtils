package dev.pixelstudios.xutils.config.serializer;

import com.cryptomorin.xseries.XPotion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectSerializer implements Serializer<PotionEffect> {

    @Override
    public PotionEffect deserialize(String serialized) {
        String[] effect = serialized.split(":");
        if (effect.length < 1) {
            throw new IllegalArgumentException("Invalid effect format: " + serialized);
        }

        PotionEffectType type = XPotion.of(effect[0]).orElse(XPotion.SPEED).get();
        int duration = effect.length > 1 ? Integer.parseInt(effect[1]) * 20 : 10 * 20;
        int amplifier = effect.length > 2 ? Integer.parseInt(effect[2]) - 1 : 0;

        return new PotionEffect(type, duration, amplifier);
    }

}
