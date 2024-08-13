package coma112.cbounty.enums.keys;

import coma112.cbounty.item.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum ItemKeys {
    BACK_ITEM("menu.back-item"),
    BOUNTYFINDER_ITEM("bountyfinder-item"),
    FORWARD_ITEM("menu.forward-item");

    private final String path;

    ItemKeys(@NotNull final String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return ItemFactory.createItemFromString(path);
    };
}
