package me.cubecrafter.xutils.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.cubecrafter.xutils.SoundUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class MenuItem {

    private static final ClickType[] DEFAULT_CLICK_TYPES = { ClickType.LEFT, ClickType.SHIFT_LEFT, ClickType.RIGHT, ClickType.SHIFT_RIGHT };

    private final ItemStack item;
    private final Map<ClickType, Consumer<InventoryClickEvent>> actions = new HashMap<>();
    private boolean cancelClick = true;
    private String clickSound;

    public MenuItem addAction(Consumer<InventoryClickEvent> action, ClickType... clickTypes) {
        if (clickTypes.length == 0) {
            clickTypes = DEFAULT_CLICK_TYPES;
        }
        for (ClickType clickType : clickTypes) {
            actions.put(clickType, action);
        }
        return this;
    }

    public MenuItem setCancelClick(boolean cancel) {
        this.cancelClick = cancel;
        return this;
    }

    public MenuItem setClickSound(String sound) {
        this.clickSound = sound;
        return this;
    }

    public void onClick(InventoryClickEvent event) {
        if (clickSound != null) {
            SoundUtil.play((Player) event.getWhoClicked(), clickSound);
        }
        if (actions.containsKey(event.getClick())) {
            actions.get(event.getClick()).accept(event);
        }
        event.setCancelled(cancelClick);
    }

}
