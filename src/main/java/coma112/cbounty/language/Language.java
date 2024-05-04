package coma112.cbounty.language;

import coma112.cbounty.CBounty;
import coma112.cbounty.utils.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Language extends ConfigUtils {
    public Language() {
        super(CBounty.getInstance().getDataFolder().getPath() + File.separator + "locales", "messages_en");

        YamlConfiguration yml = getYml();

        yml.addDefault("messages.no-permission", "&6&lBOUNTY &8| &cYou do not have permission to do this!");
        yml.addDefault("messages.reload", "&6&lBOUNTY &8| &aI have successfully reloaded the files!");
        yml.addDefault("messages.player-required", "&6&lBOUNTY &8| &cPlayer is required!");
        yml.addDefault("messages.first-page", "&6&lBOUNTY &8| &cYou are already on the first page!");
        yml.addDefault("messages.last-page", "&6&lBOUNTY &8| &cYou are already on the last page!");
        yml.addDefault("messages.setbounty-usage", "&6&lBOUNTY &8| &cUsage: /setbounty [target] [token | money] [reward]");
        yml.addDefault("messages.invalid-rewardtype", "&6&lBOUNTY &8| &cInvalid reward type!");
        yml.addDefault("messages.invalid-number", "&6&lBOUNTY &8| &cPlease enter a number!");
        yml.addDefault("messages.player-not-found", "&6&lBOUNTY &8| &cThe target is not online!");
        yml.addDefault("messages.cant-be-yourself", "&6&lBOUNTY &8| &cYou can't set yourself as the target!");
        yml.addDefault("messages.already-bounty", "&6&lBOUNTY &8| &cThe target is already bounty!");
        yml.addDefault("messages.no-negative", "&6&lBOUNTY &8| &cThe number should be positive!");
        yml.addDefault("messages.successful-set", "&6&lBOUNTY &8| &aYou have successfully set a bounty!");
        yml.addDefault("messages.bounty-dead-everyone", "&6&lBOUNTY &8| &a{name} is not a bounty anymore!");
        yml.addDefault("messages.bounty-dead-target", "&6&lBOUNTY &8| &aYou were a bounty but somebody killed you!");
        yml.addDefault("messages.bounty-dead-killer", "&6&lBOUNTY &8| &aYou have successfully killed a bounty!");
        yml.addDefault("messages.not-enough-token", "&6&lBOUNTY &8| &cYou don't have enough token!");
        yml.addDefault("messages.not-enough-money", "&6&lBOUNTY &8| &cYou don't have enough money!");
        yml.addDefault("messages.maximum-top", "&6&lBOUNTY &8| &cCan't be bigger than {top}");
        yml.addDefault("messages.top.header", "\n&6Top {value} Streaks:&f\n\n");
        yml.addDefault("messages.top.message", "&f{place}. &6{name} &f- &7(&6{streak}&7)");
        yml.addDefault("messages.max-bounty", "&6&lBOUNTY &8| &cYou can't set more bounty!");
        yml.addDefault("messages.feature-disabled", "&6&lBOUNTY &8| &cThis feature is disabled at the moment!");
        yml.addDefault("messages.feature-disabled-event", "&6&lBOUNTY &8| &cThis feature is disabled at the moment but I gave you the reward in money!");
        yml.addDefault("messages.not-a-bounty", "&6&lBOUNTY &8| &cThe target is not a bounty!");
        yml.addDefault("messages.successful-remove-player", "&6&lBOUNTY &8| &aYou have successfully removed the bounty from the target!");
        yml.addDefault("messages.successful-remove-target", "&6&lBOUNTY &8| &aYou are no longer a bounty!");

        yml.options().copyDefaults(true);
        save();
    }
}

