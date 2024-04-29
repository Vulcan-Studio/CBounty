package coma112.cbounty.utils;

import coma112.cbounty.interfaces.IConfig;
import coma112.cbounty.processor.MessageProcessor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigUtils implements IConfig {
    private YamlConfiguration yml;
    private File config;
    private String name;

    public ConfigUtils(@NotNull String dir, @NotNull String name) {
        File file = new File(dir);

        if (!file.exists()) {
            if (!file.mkdirs()) return;
        }

        config = new File(dir, name + ".yml");

        if (!config.exists()) {
            try {
                if (!config.createNewFile()) return;
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        yml = YamlConfiguration.loadConfiguration(config);
        yml.options().copyDefaults(true);
        this.name = name;
    }

    @Override
    public void reload() {
        yml = YamlConfiguration.loadConfiguration(config);
        save();
    }

    @Override
    public void set(@NotNull String path, Object value) {
        yml.set(path, value);
        save();
    }

    @Override
    public YamlConfiguration getYml() {
        return yml;
    }

    @Override
    public void save() {
        try {
            yml.save(config);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public List<String> getList(@NotNull String path) {
        return yml.getStringList(path)
                .stream()
                .map(MessageProcessor::process)
                .collect(Collectors.toList());

    }

    @Override
    public List<String> getLoreList(@NotNull String path) {
        return getList(path).stream()
                .map(MessageProcessor::process)
                .collect(Collectors.toList());
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return yml.getBoolean(path);
    }

    @Override
    public int getInt(@NotNull String path) {
        return yml.getInt(path);
    }

    @Override
    public String getString(@NotNull String path) {
        return yml.getString(path);
    }

    @Override
    public @Nullable ConfigurationSection getSection(@NotNull String path) {
        return yml.getConfigurationSection(path);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = name;
    }

}
