package dev.pixelstudios.xutils.menu;

import dev.pixelstudios.xutils.SoundUtil;
import dev.pixelstudios.xutils.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface MenuAction {

    void execute(InventoryClickEvent event, Menu menu);

    static MenuAction run(Runnable runnable) {
        return (event, menu) -> runnable.run();
    }

    static MenuAction message(String message) {
        return (event, menu) -> {
            TextUtil.sendMessage(menu.getPlayer(), menu.getPlaceholders().parse(message));
        };
    }

    static MenuAction command(String command, boolean console) {
        return (event, menu) -> {
            String parsed = menu.getPlaceholders().parse(command);

            if (console) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
            } else {
                menu.getPlayer().performCommand(parsed);
            }
        };
    }

    static MenuAction sound(String sound) {
        return (event, menu) -> {
            SoundUtil.play(menu.getPlayer(), sound);
        };
    }

    static MenuAction close() {
        return (event, menu) -> {
            menu.close();
        };
    }

    static MenuAction parse(String actionString) {
        String[] parts = actionString.split(":", 2);
        String type = parts[0].toLowerCase().trim();

        if (parts.length == 1) {
            if (type.equals("close")) {
                return MenuAction.close();
            }
        } else if (parts.length == 2) {
            String args = parts[1].trim();

            switch (type) {
                case "message":
                    return MenuAction.message(args);
                case "console":
                    return MenuAction.command(args, true);
                case "command":
                    return MenuAction.command(args, false);
                case "sound":
                    return MenuAction.sound(args);
            }
        }

        return null;
    }

}
