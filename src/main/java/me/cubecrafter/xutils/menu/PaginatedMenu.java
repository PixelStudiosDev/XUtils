package me.cubecrafter.xutils.menu;

import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class PaginatedMenu extends Menu {

    @Getter
    private int page;

    public PaginatedMenu(Player player) {
        super(player);
    }

    @Override
    public void update() {
        update(page);
    }

    public boolean nextPage() {
        if (page < Math.max(0, getMaxPages() - 1)) {
            update(++page);
            return true;
        }
        return false;
    }

    public boolean previousPage() {
        if (page > 0) {
            update(--page);
            return true;
        }
        return false;
    }

    public boolean isFirstPage() {
        return page == 0;
    }

    public boolean isLastPage() {
        return page == getMaxPages() - 1;
    }

    public abstract int getMaxPages();
    public abstract void update(int page);

}
