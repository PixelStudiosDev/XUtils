package dev.pixelstudios.xutils.menu;

import dev.pixelstudios.xutils.SoundUtil;
import dev.pixelstudios.xutils.item.ItemBuilder;
import dev.pixelstudios.xutils.objects.PlaceholderMap;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public class MenuItem {

    private static final ClickType[] DEFAULT_CLICK_TYPES = {
            ClickType.LEFT,
            ClickType.SHIFT_LEFT,
            ClickType.RIGHT,
            ClickType.SHIFT_RIGHT
    };

    private final ItemBuilder item;
    private final Set<Integer> slots = new HashSet<>();
    private final Map<ClickType, Consumer<InventoryClickEvent>> actions = new HashMap<>();

    private boolean cancelClick = true;
    private String sound;

    public MenuItem(ItemStack item) {
        this.item = new ItemBuilder(item);
    }

    public MenuItem(ItemBuilder item) {
        this.item = item;
    }

    public MenuItem(ConfigurationSection section) {
        this(section, null);
    }

    public MenuItem(ConfigurationSection section, ItemBuilder defaultItem) {
        this(ItemBuilder.fromConfig(section, defaultItem));

        if (section.isList("slots")) {
            slots.addAll(section.getIntegerList("slots"));
        } else if (section.isInt("slot")) {
            slots.add(section.getInt("slot"));
        }
    }

    public MenuItem action(Consumer<InventoryClickEvent> action, ClickType... clickTypes) {
        if (clickTypes.length == 0) {
            clickTypes = DEFAULT_CLICK_TYPES;
        }

        for (ClickType clickType : clickTypes) {
            actions.put(clickType, action);
        }
        return this;
    }

    public MenuItem action(Runnable action, ClickType... clickTypes) {
        return action(event -> action.run(), clickTypes);
    }

    public MenuItem leftAction(Runnable action) {
        return action(action, ClickType.LEFT, ClickType.SHIFT_LEFT);
    }

    public MenuItem rightAction(Runnable action) {
        return action(action, ClickType.RIGHT, ClickType.SHIFT_RIGHT);
    }

    public MenuItem cancelClick(boolean cancel) {
        this.cancelClick = cancel;
        return this;
    }

    public MenuItem sound(String sound) {
        this.sound = sound;
        return this;
    }

    public MenuItem sound(Sound sound) {
        this.sound = sound.toString();
        return this;
    }

    public MenuItem placeholders(PlaceholderMap placeholders) {
        item.placeholders(placeholders);
        return this;
    }

    public MenuItem placeholder(String target, String replacement) {
        item.placeholder(target, replacement);
        return this;
    }

    public MenuItem slots(int... slots) {
        for (int slot : slots) {
            this.slots.add(slot);
        }
        return this;
    }

    public void onClick(InventoryClickEvent event) {
        ClickType clickType = event.getClick();

        if (actions.containsKey(clickType)) {
            actions.get(clickType).accept(event);

            SoundUtil.play((Player) event.getWhoClicked(), sound);
        }

        event.setCancelled(cancelClick);
    }

}
