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

        yml.addDefault("placeholder.yes", "&aIgen");
        yml.addDefault("placeholder.no", "&cNem");

        yml.addDefault("dependency.tokenmanager", true);

        yml.addDefault("bounty-item.amount", 1);
        yml.addDefault("bounty-item.material", "PAPER");
        yml.addDefault("bounty-item.name", "&c&l! &6{target} &8(&7{id}&8) &c&l!");
        yml.addDefault("bounty-item.lore", List.of(
                "",
                "&6Sender: &f{player} &c⚡",
                "&6Reward: &f{reward} &c⚡",
                "&6Reward Type: &f{reward_type} &c⚡",
                "&6Streak: &f{streak} &c🔥"
        ));

        yml.addDefault("max.top-to-get", 15);
        yml.addDefault("max.bounty-per-player", 5);

        yml.addDefault("menu.title", "&6&lBOUNTIES");
        yml.addDefault("menu.size", 54);
        yml.addDefault("menu.update-tick", 2);

        yml.addDefault("menu.back-item.amount", 1);
        yml.addDefault("menu.back-item.material", "RED_STAINED_GLASS");
        yml.addDefault("menu.back-item.name", "&cBack");
        yml.addDefault("menu.back-item.slot", 45);

        yml.addDefault("menu.forward-item.amount", 1);
        yml.addDefault("menu.forward-item.material", "GREEN_STAINED_GLASS");
        yml.addDefault("menu.forward-item.name", "&aForward");
        yml.addDefault("menu.forward-item.slot", 53);

        yml.options().copyDefaults(true);
        save();
    }

}

