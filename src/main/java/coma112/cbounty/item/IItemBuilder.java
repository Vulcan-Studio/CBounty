package coma112.cbounty.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public interface IItemBuilder {
    static IItemBuilder create(@NotNull Material material) {
        return new ItemBuilder(material);
    }

    static IItemBuilder create(@NotNull Material material, int count) {
        return new ItemBuilder(material, count);
    }

    static IItemBuilder create(@NotNull Material material, int count, short damage) {
        return new ItemBuilder(material, count, damage);
    }

    static IItemBuilder create(@NotNull Material material, int count, short damage, byte data) {
        return new ItemBuilder(material, count, damage, data);
    }

    static IItemBuilder create(ItemStack item) {
        return new ItemBuilder(item);
    }

    IItemBuilder setType(@NotNull Material material);

    IItemBuilder setCount(int newCount);

    IItemBuilder setName(@NotNull String name);

    ItemBuilder setLocalizedName(@NotNull String name);

    ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level);

    default IItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);

        return this;
    }

    ItemBuilder addLore(@NotNull String... lores);

    IItemBuilder setUnbreakable();

    default void addFlag(@NotNull ItemFlag... flags) {
        Arrays
                .stream(flags)
                .forEach(this::addFlag);
    }

    default IItemBuilder setLore(@NotNull String... lores) {
        Arrays
                .stream(lores)
                .forEach(this::addLore);
        return this;
    }

    IItemBuilder removeLore(int line);

    ItemStack finish();

    boolean isFinished();
}



