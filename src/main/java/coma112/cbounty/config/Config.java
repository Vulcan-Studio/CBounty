package coma112.cbounty.config;

import coma112.cbounty.CBounty;
import coma112.cbounty.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class Config extends ConfigUtils {
    public Config() {
        super(CBounty.getInstance().getDataFolder().getPath(), "config");

        YamlConfiguration yml = getYml();

        yml.addDefault("database.type", "mysql");
        yml.addDefault("database.mysql.host", "localhost");
        yml.addDefault("database.mysql.port", 3306);
        yml.addDefault("database.mysql.database", "coma112");
        yml.addDefault("database.mysql.username", "root");
        yml.addDefault("database.mysql.password", "");
        yml.addDefault("database.mysql.ssl", false);
        yml.addDefault("database.mysql.certificateverification", false);
        yml.addDefault("database.mysql.poolsize", 10);
        yml.addDefault("database.mysql.lifetime", 1800000);

        yml.addDefault("bounty-item.material", "PAPER");
        yml.addDefault("bounty-item.name", "&c&l! &6{target} &8(&7{id}&8) &c&l!");
        yml.addDefault("bounty-item.lore", List.of(
                "",
                "&6Sender: &f{player}",
                "&6Reward: &f{reward}",
                "&6Reward Type: &f{reward_type}"
        ));


        yml.options().copyDefaults(true);
        save();
    }

}

