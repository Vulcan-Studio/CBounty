package coma112.cbounty.commands;

import coma112.cbounty.CBounty;
import coma112.cbounty.database.AbstractDatabase;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.keys.MessageKeys;
import coma112.cbounty.hooks.Token;
import coma112.cbounty.hooks.vault.Vault;
import coma112.cbounty.managers.Top;
import coma112.cbounty.menu.menus.BountiesMenu;
import coma112.cbounty.utils.MenuUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("deprecation")
@Command({"bounty", "cbounty"})
public class CommandBounty {

    @Subcommand("reload")
    public void reload(CommandSender sender) {
        if (!sender.hasPermission("cbounty.reload")) {
            sender.sendMessage(MessageKeys.NO_PERMISSION.getMessage());
            return;
        }

        CBounty.getInstance().getLanguage().reload();
        CBounty.getInstance().getConfiguration().reload();
        CBounty.getDatabaseManager().reconnect(Objects.requireNonNull(CBounty.getInstance().getConfiguration().getSection("database.mysql")));
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("streaktop")
    public void streaktop(CommandSender sender, int value) {
        if (value == 0) {
            sender.spigot().sendMessage(Top.getTopStreak(10));
            return;
        }

        try {
            value = Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException exception) {
            sender.sendMessage(MessageKeys.INVALID_NUMBER.getMessage());
        }


        if (value <= 0) {
            sender.sendMessage(MessageKeys.NO_NEGATIVE.getMessage());
            return;
        }

        if (value > 15) {
            sender.sendMessage(MessageKeys.MAX_TOP
                    .getMessage()
                    .replace("{top}", String.valueOf(ConfigKeys.MAXIMUM_TOP.getInt())));
            return;
        }

        sender.spigot().sendMessage(Top.getTopStreak(value));
    }

    @Subcommand("menu")
    public void menu(@NotNull Player player) {
        new BountiesMenu(MenuUtils.getMenuUtils(player)).open();
    }



    @Subcommand("set")
    public void set(@NotNull Player player, @NotNull Player target, RewardType rewardType, int reward) {
        if (!target.isOnline()) {
            player.sendMessage(MessageKeys.PLAYER_NOT_FOUND.getMessage());
            return;
        }

        if (target == player) {
            player.sendMessage(MessageKeys.CANT_BE_YOURSELF.getMessage());
            return;
        }

        if (CBounty.getDatabaseManager().isBounty(target)) {
            player.sendMessage(MessageKeys.ALREADY_BOUNTY.getMessage());
            return;
        }

        if (reward <= 0) {
            player.sendMessage(MessageKeys.NO_NEGATIVE.getMessage());
            return;
        }

        Token token = CBounty.getInstance().getToken();
        AbstractDatabase databaseManager = CBounty.getDatabaseManager();

        Optional.ofNullable(token)
                .filter(Token::isEnabled)
                .ifPresentOrElse(
                        tokenManager -> {
                            if (rewardType == RewardType.TOKEN) {
                                int playerBalance = CBounty.getInstance().getToken().getTokens(player);

                                if (playerBalance < reward) {
                                    player.sendMessage(MessageKeys.NOT_ENOUGH_TOKEN.getMessage());
                                    return;
                                }

                                CBounty.getTokenManager().removeTokens(player, reward);
                            } else if (rewardType == RewardType.MONEY) {
                                Economy economy = Vault.getEconomy();
                                double playerBalance = economy.getBalance(player);

                                if (playerBalance < reward) {
                                    player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY.getMessage());
                                    return;
                                }

                                economy.depositPlayer(player, reward);
                            }

                            databaseManager.createBounty(player, target, rewardType, reward);
                            player.sendMessage(MessageKeys.SUCCESSFUL_SET.getMessage());
                        },
                        () -> player.sendMessage(MessageKeys.FEATURE_DISABLED.getMessage())
                );
    }


    @Subcommand("remove")
    public void remove(@NotNull Player player, @NotNull Player target) {
        if (!player.hasPermission("cbounty.remove")) {
            player.sendMessage(MessageKeys.NO_PERMISSION.getMessage());
            return;
        }

        if (!target.isOnline()) {
            player.sendMessage(MessageKeys.PLAYER_NOT_FOUND.getMessage());
            return;
        }

        if (!CBounty.getDatabaseManager().isBounty(target)) {
            player.sendMessage(MessageKeys.NOT_BOUNTY.getMessage());
            return;
        }

        CBounty.getDatabaseManager().removeBounty(target);
        player.sendMessage(MessageKeys.REMOVE_PLAYER.getMessage());
        target.sendMessage(MessageKeys.REMOVE_TARGET.getMessage());
    }
}
