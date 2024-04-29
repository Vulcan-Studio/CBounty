package coma112.cbounty.menu;

import coma112.cbounty.storage.ItemStorage;
import coma112.cbounty.utils.MenuUtils;
import lombok.Getter;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    @Getter
    protected int maxItemsPerPage = 50;
    protected int index = 0;

    public PaginatedMenu(MenuUtils menuUtils) {
        super(menuUtils);
    }

    public void addMenuBorder() {
        inventory.setItem(45, ItemStorage.BACK);
        inventory.setItem(53, ItemStorage.FORWARD);
    }
}

