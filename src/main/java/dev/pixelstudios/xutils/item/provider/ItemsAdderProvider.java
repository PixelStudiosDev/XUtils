package dev.pixelstudios.xutils.item.provider;

import dev.lone.itemsadder.api.CustomStack;
import dev.pixelstudios.xutils.item.ItemProvider;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderProvider extends ItemProvider {

    @Override
    public String[] getIds() {
        return new String[] {
            "itemsadder", "ia"
        };
    }

    @Override
    public String getPlugin() {
        return "ItemsAdder";
    }

    @Override
    public ItemStack getItem(String item) {
        CustomStack stack = CustomStack.getInstance(item);
        return stack == null ? null : stack.getItemStack();
    }

    @Override
    public String getItemKey(ItemStack item) {
        CustomStack stack = CustomStack.byItemStack(item);
        return stack == null ? null : stack.getId();
    }

}
