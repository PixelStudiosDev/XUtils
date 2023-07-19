package me.cubecrafter.xutils.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.xutils.ItemBuilder;
import me.cubecrafter.xutils.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public class MenuItem {

    private static final ClickType[] DEFAULT_CLICK_TYPES = {
            ClickType.LEFT,
            ClickType.SHIFT_LEFT,
            ClickType.RIGHT,
            ClickType.SHIFT_RIGHT
    };

    private final ItemStack item;
    private final Map<ClickType, Predicate<InventoryClickEvent>> actions = new HashMap<>();

    private boolean cancelClick = true;
    private String sound;

    public MenuItem(ItemBuilder item) {
        this(item.build());
    }

    public MenuItem addAction(Predicate<InventoryClickEvent> action, ClickType... clickTypes) {
        if (clickTypes.length == 0) {
            clickTypes = DEFAULT_CLICK_TYPES;
        }
        for (ClickType clickType : clickTypes) {
            actions.put(clickType, action);
        }
        return this;
    }

    public MenuItem addAction(BooleanSupplier action, ClickType... clickTypes) {
        return addAction(event -> action.getAsBoolean(), clickTypes);
    }

    public MenuItem addAction(Runnable action, ClickType... clickTypes) {
        return addAction(event -> {
            action.run();
            return true;
        }, clickTypes);
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

    public void onClick(InventoryClickEvent event) {
        ClickType clickType = event.getClick();

        if (actions.containsKey(clickType)) {
            Predicate<InventoryClickEvent> action = actions.get(clickType);

            if (action.test(event) && sound != null) {
                SoundUtil.play((Player) event.getWhoClicked(), sound);
            }
        }

        event.setCancelled(cancelClick);
    }

}
