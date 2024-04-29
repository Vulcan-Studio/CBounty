package coma112.cbounty.commands;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.hooks.Vault;
import coma112.cbounty.language.MessageKeys;
import coma112.cbounty.subcommand.CommandInfo;
import coma112.cbounty.subcommand.PluginCommand;
import net.coma.ctoken.CToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@CommandInfo(name = "setbounty", requiresPlayer = true)
public class CommandSetBounty extends PluginCommand {
    public CommandSetBounty() {
        super("setbounty");
    }

    @Override
    public boolean run(@NotNull Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(MessageKeys.SET_BOUNTY_USAGE);
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        RewardType rewardType;
        int reward;

        if (!Objects.requireNonNull(target).isOnline()) {
            player.sendMessage(MessageKeys.PLAYER_NOT_FOUND);
            return true;
        }

        if (target == player) {
            player.sendMessage(MessageKeys.CANT_BE_YOURSELF);
            return true;
        }

        if (CBounty.getDatabaseManager().isBounty(target)) {
            player.sendMessage(MessageKeys.ALREADY_BOUNTY);
            return true;
        }

        try {
            rewardType = RewardType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException exception) {
            player.sendMessage(MessageKeys.INVALID_REWARDTYPE);
            return true;
        }

        try {
            reward = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            player.sendMessage(MessageKeys.INVALID_NUMBER);
            return true;
        }

        if (reward <= 0) {
            player.sendMessage(MessageKeys.NO_NEGATIVE);
            return true;
        }

        switch (rewardType) {
            case TOKEN -> {
                if (CToken.getInstance().getDatabaseManager().getBalance(player) < reward) {
                    player.sendMessage(MessageKeys.NOT_ENOUGH_TOKEN);
                    return true;
                }

                if (CToken.getInstance().getDatabaseManager().getBalance(player) >= reward) CToken.getInstance().getDatabaseManager().takeFromBalance(player, reward);
            }

            case MONEY -> {
                if (Vault.getEconomy().getBalance(player) < reward) {
                    player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY);
                    return true;
                }

                if (Vault.getEconomy().getBalance(player) >= reward) Vault.getEconomy().depositPlayer(player, reward);
            }
        }

        CBounty.getDatabaseManager().createBounty(player, target, rewardType, reward);
        player.sendMessage(MessageKeys.SUCCESSFUL_SET);
        return true;
    }
}
