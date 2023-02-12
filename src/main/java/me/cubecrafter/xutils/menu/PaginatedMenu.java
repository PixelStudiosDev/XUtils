package me.cubecrafter.xutils.menu;

import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class PaginatedMenu extends Menu {

    @Getter
    private int page = 1;

    public PaginatedMenu(Player player) {
        super(player);
    }

    @Override
    public void update() {
        update(page);
    }

    public boolean nextPage() {
        if (page < Math.max(1, getMaxPages())) {
            update(++page);
            return true;
        }
        return false;
    }

    public boolean previousPage() {
        if (page > 1) {
            update(--page);
            return true;
        }
        return false;
    }

    public boolean isFirstPage() {
        return page == 1;
    }

    public boolean isLastPage() {
        return page == Math.max(1, getMaxPages());
    }

    public abstract int getMaxPages();
    public abstract void update(int page);

}
