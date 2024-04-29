package coma112.cbounty.language;

import coma112.cbounty.CBounty;
import coma112.cbounty.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Language extends ConfigUtils {
    public Language() {
        super(CBounty.getInstance().getDataFolder().getPath() + File.separator + "locales", "messages_en");

        YamlConfiguration yml = getYml();

        yml.addDefault("prefix", "&b&lAUCTION &8| ");
        yml.addDefault("messages.no-permission", "&cYou do not have permission to do this!");
        yml.addDefault("messages.reload", "&aI have successfully reloaded the files!");
        yml.addDefault("messages.player-required", "&cPlayer is required!");
        yml.addDefault("messages.first-page", "&cYou are already on the first page!");
        yml.addDefault("messages.last-page", "&cYou are already on the last page!");
        yml.addDefault("messages.setbounty-usage", "&cUsage: /setbounty [target] [token | money] [reward]");
        yml.addDefault("messages.invalid-rewardtype", "&cInvalid reward type!");
        yml.addDefault("messages.invalid-number", "&cPlease enter a number!");
        yml.addDefault("messages.player-not-found", "&cThe target is not online!");
        yml.addDefault("messages.cant-be-yourself", "&cYou can't set yourself as the target!");
        yml.addDefault("messages.already-bounty", "&cThe target is already bounty!");
        yml.addDefault("messages.no-negative", "&cThe number should be positive!");
        yml.addDefault("messages.successful-set", "&aYou have successfully set a bounty!");

        yml.options().copyDefaults(true);
        save();
    }
}

