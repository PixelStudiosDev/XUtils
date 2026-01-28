package dev.pixelstudios.xutils.menu;

import dev.pixelstudios.xutils.SoundUtil;
import dev.pixelstudios.xutils.item.ItemBuilder;
import dev.pixelstudios.xutils.objects.MultiMap;
import dev.pixelstudios.xutils.text.TextUtil;
import dev.pixelstudios.xutils.text.placeholder.PlaceholderMap;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class MenuItem implements Cloneable {

    private static final ClickType[] DEFAULT_CLICK_TYPES = {
            ClickType.LEFT,
            ClickType.SHIFT_LEFT,
            ClickType.RIGHT,
            ClickType.SHIFT_RIGHT
    };

    private static final Map<String, ClickType> CLICK_TYPES = new HashMap<>();

    static {
        CLICK_TYPES.put("left", ClickType.LEFT);
        CLICK_TYPES.put("right", ClickType.RIGHT);
        CLICK_TYPES.put("shift-left", ClickType.SHIFT_LEFT);
        CLICK_TYPES.put("shift-right", ClickType.SHIFT_RIGHT);
    }

    private final ItemBuilder item;
    private final Set<Integer> slots = new HashSet<>();
    private final MultiMap<ClickType, MenuAction> actions = new MultiMap<>();

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

    public MenuItem(ConfigurationSection section, ItemBuilder fallbackItem) {
        this(ItemBuilder.fromConfig(section, fallbackItem));

        if (section.isList("slots")) {
            slots.addAll(section.getIntegerList("slots"));
        } else if (section.isInt("slot")) {
            slots.add(section.getInt("slot"));
        }

        if (section.isList("actions")) {
            section.getStringList("actions").forEach(this::action);
        } else if (section.isConfigurationSection("actions")) {
            ConfigurationSection actionsSection = section.getConfigurationSection("actions");

            for (String key : actionsSection.getKeys(false)) {
                ClickType clickType = CLICK_TYPES.get(key.toLowerCase());

                if (clickType != null) {
                    actionsSection.getStringList(key).forEach(string -> {
                        action(string, clickType);
                    });
                } else {
                    TextUtil.error("Invalid click type for menu action: " + key);
                }
            }
        }
    }

    public MenuItem action(MenuAction action, ClickType... clickTypes) {
        if (clickTypes.length == 0) {
            clickTypes = DEFAULT_CLICK_TYPES;
        }

        for (ClickType clickType : clickTypes) {
            actions.add(clickType, action);
        }

        return this;
    }

    public MenuItem action(String actionString, ClickType... clickTypes) {
        MenuAction action = MenuAction.parse(actionString);

        if (action == null) {
            TextUtil.error("Invalid menu action: " + actionString);
            return this;
        }

        return action(action, clickTypes);
    }

    public MenuItem action(Runnable action, ClickType... clickTypes) {
        return action(MenuAction.run(action), clickTypes);
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

    public void onClick(InventoryClickEvent event, Menu menu) {
        ClickType clickType = event.getClick();

        if (actions.containsKey(clickType)) {
            actions.forEach(clickType, action -> {
                action.execute(event, menu);
            });

            SoundUtil.play(menu.getPlayer(), sound);
        }

        event.setCancelled(cancelClick);
    }

    @Override
    public MenuItem clone() {
        MenuItem clone = new MenuItem(this.item.clone());

        clone.actions.putAll(this.actions);
        clone.slots.addAll(this.slots);
        clone.cancelClick = this.cancelClick;
        clone.sound = this.sound;

        return clone;
    }

}

