package coma112.cbounty.item;

import coma112.cbounty.CBounty;
import coma112.cbounty.processor.MessageProcessor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public interface ItemFactory {
    static ItemFactory create(@NotNull Material material) {
        return new ItemBuilder(material);
    }

    static ItemFactory create(@NotNull Material material, int count) {
        return new ItemBuilder(material, count);
    }

    static ItemFactory create(@NotNull Material material, int count, short damage) {
        return new ItemBuilder(material, count, damage);
    }

    static ItemFactory create(@NotNull Material material, int count, short damage, byte data) {
        return new ItemBuilder(material, count, damage, data);
    }

    static ItemFactory create(ItemStack item) {
        return new ItemBuilder(item);
    }

    static ItemStack createItemFromString(@NotNull String path) {
        ConfigurationSection section = CBounty.getInstance().getConfiguration().getSection(path);

        Material material = Material.valueOf(Objects.requireNonNull(section).getString("material"));
        int amount = section.getInt("amount", 1);
        String name = section.getString("name");
        String[] loreArray = section.getStringList("lore").toArray(new String[0]);

        for (int i = 0; i < loreArray.length; i++) loreArray[i] = MessageProcessor.process(loreArray[i]);

        return ItemFactory.create(material, amount)
                .setName(Objects.requireNonNull(name))
                .addLore(loreArray)
                .finish();
    }

    ItemFactory setType(@NotNull Material material);

    ItemFactory setCount(int newCount);

    void addEnchantment(@NotNull Enchantment enchantment, int level);

    ItemFactory setName(@NotNull String name);

    ItemBuilder addLore(@NotNull String... lores);

    default ItemFactory addEnchantments(Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);

        return this;
    }

    default void addFlag(@NotNull ItemFlag... flags) {
        Arrays
                .stream(flags)
                .forEach(this::addFlag);
    }

    ItemFactory setUnbreakable();

    default ItemFactory setLore(@NotNull String... lores) {
        Arrays
                .stream(lores)
                .forEach(this::addLore);
        return this;
    }

    ItemStack finish();

    boolean isFinished();

    ItemFactory removeLore(int line);
}



