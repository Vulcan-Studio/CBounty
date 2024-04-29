package coma112.cbounty.storage;

import coma112.cbounty.item.IItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStorage {
    public static final ItemStack BACK = IItemBuilder.create(Material.RED_STAINED_GLASS)
            .setName("&cBack")
            .finish();

    public static final ItemStack FORWARD = IItemBuilder.create(Material.GREEN_STAINED_GLASS)
            .setName("&aForward")
            .finish();
}
