package coma112.cbounty.interfaces;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IConfig {
    void reload();

    void set(@NotNull String path, Object value);

    YamlConfiguration getYml();

    void save();

    List<String> getList(@NotNull String path);

    List<String> getLoreList(@NotNull String path);

    boolean getBoolean(@NotNull String path);

    int getInt(@NotNull String path);

    String getString(@NotNull String path);

    @Nullable ConfigurationSection getSection(@NotNull String path);

    String getName();

    void setName(@NotNull String name);
}


