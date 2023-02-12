package me.cubecrafter.xutils.menu;

import com.cryptomorin.xseries.XSound;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class MenuItem {

    private static final ClickType[] DEFAULT_CLICK_TYPES = { ClickType.LEFT, ClickType.SHIFT_LEFT, ClickType.RIGHT, ClickType.SHIFT_RIGHT };

    private final ItemStack item;
    private final Map<ClickType, Consumer<InventoryClickEvent>> actions = new HashMap<>();

    private String sound;
    private boolean cancelClick = true;

    private MenuItem(ItemStack item) {
        this.item = item;
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

    public MenuItem cancelClick(boolean cancelClick) {
        this.cancelClick = cancelClick;
        return this;
    }

    public MenuItem sound(String sound) {
        this.sound = sound;
        return this;
    }

    public void onClick(InventoryClickEvent event) {
        if (sound != null) {
            Player player = (Player) event.getWhoClicked();
            XSound.play(player, sound);
        }
        if (actions.containsKey(event.getClick())) {
            actions.get(event.getClick()).accept(event);
        }
        event.setCancelled(cancelClick);
    }

    public static MenuItem of(ItemStack item) {
        return new MenuItem(item);
    }

    public static MenuItem empty() {
        return new MenuItem(new ItemStack(Material.AIR));
    }

}
