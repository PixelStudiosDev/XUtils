package dev.pixelstudios.xutils.item.provider;

import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import dev.pixelstudios.xutils.item.ItemProvider;
import org.bukkit.inventory.ItemStack;

public class CustomHeadProvider extends ItemProvider {

    @Override
    public String[] getIds() {
        return new String[] {
            "texture"
        };
    }

    @Override
    public String getPlugin() {
        return null;
    }

    @Override
    public ItemStack getItem(String item) {
        return XSkull.createItem().profile(Profileable.detect(item)).apply();
    }

    @Override
    public String getItemKey(ItemStack item) {
        return null;
    }

}
