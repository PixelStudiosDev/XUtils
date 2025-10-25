package dev.pixelstudios.xutils.config.serializer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class AttributeModifierSerializer implements Serializer<AttributeModifierSerializer.AttributeModifierWrapper> {

    @Override
    public AttributeModifierWrapper deserialize(String serialized) {
        String[] split = serialized.split(":");

        if (split.length < 2) {
            throw new IllegalArgumentException("Invalid attribute modifier format: " + serialized);
        }

        Attribute attribute = Registry.ATTRIBUTE.match(split[0].toUpperCase());
        AttributeModifier modifier = new AttributeModifier(
                UUID.randomUUID(),
                "custom_modifier",
                Double.parseDouble(split[1]),
                AttributeModifier.Operation.ADD_NUMBER,
                split.length == 3 ? EquipmentSlot.valueOf(split[2].toUpperCase()) : null
        );

        return new AttributeModifierWrapper(attribute, modifier);
    }

    @Getter
    @RequiredArgsConstructor
    public static class AttributeModifierWrapper {

        private final Attribute attribute;
        private final AttributeModifier modifier;

    }

}
