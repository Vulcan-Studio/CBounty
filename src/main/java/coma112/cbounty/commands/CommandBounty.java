package coma112.cbounty.commands;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.keys.MessageKeys;
import coma112.cbounty.managers.Top;
import coma112.cbounty.menu.menus.BountiesMenu;
import coma112.cbounty.utils.MenuUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Subcommand;

import java.util.Objects;

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
    public void menu(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageKeys.PLAYER_REQUIRED.getMessage());
            return;
        }

        new BountiesMenu(MenuUtils.getMenuUtils(player)).open();
    }

    @Subcommand("set")
    public void set(CommandSender sender, Player target, RewardType rewardType, int reward) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageKeys.PLAYER_REQUIRED.getMessage());
            return;
        }

        if (!Objects.requireNonNull(target).isOnline()) {
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

        try {
            rewardType = RewardType.valueOf(String.valueOf(reward).toUpperCase());
        } catch (IllegalArgumentException exception) {
            player.sendMessage(MessageKeys.INVALID_REWARDTYPE.getMessage());
            return;
        }

        try {
            reward = Integer.parseInt(String.valueOf(reward));
        } catch (NumberFormatException exception) {
            player.sendMessage(MessageKeys.INVALID_NUMBER.getMessage());
            return;
        }

        if (reward <= 0) {
            player.sendMessage(MessageKeys.NO_NEGATIVE.getMessage());
            return;
        }

        if (CBounty.getDatabaseManager().reachedMaximumBounty(player)) {
            player.sendMessage(MessageKeys.MAX_BOUNTY.getMessage());
            return;
        }

        switch (rewardType) {
            case TOKEN -> CBounty.getInstance().getBountyManager().tryToSetBountyWithToken(player, reward);
            case MONEY -> CBounty.getInstance().getBountyManager().tryToSetBountyWithVault(player, reward);
        }

        CBounty.getDatabaseManager().createBounty(player, target, rewardType, reward);
        player.sendMessage(MessageKeys.SUCCESSFUL_SET.getMessage());
    }
}
