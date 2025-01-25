package dev.pixelstudios.xutils.item.provider;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import dev.pixelstudios.xutils.item.ItemProvider;
import org.bukkit.inventory.ItemStack;

public class NexoProvider extends ItemProvider {

    @Override
    public String[] getIds() {
        return new String[] {
            "nexo"
        };
    }

    @Override
    public String getPlugin() {
        return "Nexo";
    }

    @Override
    public ItemStack getItem(String item) {
        ItemBuilder builder = NexoItems.itemFromId(item);
        return builder == null ? null : builder.build();
    }

    @Override
    public String getItemKey(ItemStack item) {
        return NexoItems.idFromItem(item);
    }

}
