package coma112.cbounty.commands;

import coma112.cbounty.CBounty;
import coma112.cbounty.database.AbstractDatabase;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.keys.MessageKeys;
import coma112.cbounty.events.BountyRemoveEvent;
import coma112.cbounty.hooks.Token;
import coma112.cbounty.hooks.vault.Vault;
import coma112.cbounty.managers.Top;
import coma112.cbounty.menu.menus.BountiesMenu;
import coma112.cbounty.utils.MenuUtils;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("deprecation")
@Command({"bounty", "cbounty"})
public class CommandBounty {

    @Subcommand("reload")
    public void reload(@NotNull CommandSender sender) {
        if (!sender.hasPermission("cbounty.reload") || !sender.hasPermission("cbounty.admin")) {
            sender.sendMessage(MessageKeys.NO_PERMISSION.getMessage());
            return;
        }

        CBounty.getInstance().getLanguage().reload();
        CBounty.getInstance().getConfiguration().reload();
        CBounty.getDatabaseManager().reconnect(Objects.requireNonNull(CBounty.getInstance().getConfiguration().getSection("database.mysql")));
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("streaktop")
    public void streaktop(@NotNull CommandSender sender, int value) {
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
    public void set(@NotNull CommandSender sender, @NotNull Player target, RewardType rewardType, int reward) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageKeys.PLAYER_REQUIRED.getMessage());
            return;
        }

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

        if (reward > ConfigKeys.MAX_REWARD_LIMIT.getInt()) {
            player.sendMessage(MessageKeys.MAX_REWARD_LIMIT
                    .getMessage()
                    .replace("{limit}", String.valueOf(ConfigKeys.MAX_REWARD_LIMIT.getInt())));
            return;
        }

        if (CBounty.getDatabaseManager().reachedMaximumBounty(player)) {
            player.sendMessage(MessageKeys.MAX_BOUNTY.getMessage());
            return;
        }

        Token token = CBounty.getInstance().getToken();
        AbstractDatabase databaseManager = CBounty.getDatabaseManager();

        Optional.ofNullable(token)
                .filter(Token::isEnabled)
                .ifPresentOrElse(
                        tokenManager -> {
                            switch (rewardType) {
                                case TOKEN -> handleTokenReward(player, reward);
                                case MONEY -> handleMoneyReward(player, reward);
                                case PLAYERPOINTS -> handlePlayerPointsReward(player, reward);
                            }

                            databaseManager.createBounty(player, target, rewardType, reward);
                            player.sendMessage(MessageKeys.SUCCESSFUL_SET.getMessage());
                        }, () -> player.sendMessage(MessageKeys.FEATURE_DISABLED.getMessage())
                );
    }

    @Subcommand("remove")
    public void remove(@NotNull CommandSender sender, @NotNull Player target) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageKeys.PLAYER_REQUIRED.getMessage());
            return;
        }

        if (!player.hasPermission("cbounty.remove") || !player.hasPermission("cbounty.admin")) {
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
        CBounty.getInstance().getServer().getPluginManager().callEvent(new BountyRemoveEvent(player, target));
    }

    @Subcommand("raise")
    public void raise(@NotNull CommandSender sender, @NotNull Player target, int newReward) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageKeys.PLAYER_REQUIRED.getMessage());
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

        if (newReward <= 0) {
            player.sendMessage(MessageKeys.NO_NEGATIVE.getMessage());
            return;
        }

        if (newReward > ConfigKeys.MAX_REWARD_LIMIT.getInt()) {
            player.sendMessage(MessageKeys.MAX_REWARD_LIMIT.getMessage());
            return;
        }

        if (CBounty.getDatabaseManager().getSender(target) != player) {
            player.sendMessage(MessageKeys.NOT_MATCHING_OWNERS.getMessage());
            return;
        }

        int oldReward = CBounty.getDatabaseManager().getReward(target);

        CBounty.getDatabaseManager().changeReward(target, newReward);
        player.sendMessage(MessageKeys.PLAYER_RAISE.getMessage());
        target.sendMessage(MessageKeys.TARGET_RAISE
                .getMessage()
                .replace("{old}", String.valueOf(oldReward))
                .replace("{new}", String.valueOf(oldReward + newReward)));
    }

    @Subcommand("takeoff")
    public void takeoff(@NotNull CommandSender sender, @NotNull Player target) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageKeys.PLAYER_REQUIRED.getMessage());
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

        if (CBounty.getDatabaseManager().getSender(target) != player) {
            player.sendMessage(MessageKeys.NOT_MATCHING_OWNERS.getMessage());
            return;
        }


        switch (CBounty.getDatabaseManager().getRewardType(target)) {
            case TOKEN -> CBounty.getTokenManager().addTokens(player, CBounty.getDatabaseManager().getReward(target));
            case MONEY -> Vault.getEconomy().depositPlayer(player, CBounty.getDatabaseManager().getReward(target));
            case PLAYERPOINTS -> CBounty.getPlayerPointsManager().give(player.getUniqueId(), CBounty.getDatabaseManager().getReward(target));
        }

        player.sendMessage(MessageKeys.SUCCESSFUL_TAKEOFF_PLAYER
                .getMessage()
                .replace("{target}", target.getName()));

        target.sendMessage(MessageKeys.SUCCESSFUL_TAKEOFF_TARGET
                .getMessage()
                .replace("{player}", player.getName()));
        CBounty.getDatabaseManager().removeBounty(target);
        CBounty.getInstance().getServer().getPluginManager().callEvent(new BountyRemoveEvent(player, target));
    }

    private void handleTokenReward(@NotNull Player player, int reward) {
        if (CBounty.getInstance().getToken().getTokens(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_TOKEN.getMessage());
            return;
        }
        CBounty.getTokenManager().removeTokens(player, reward);
    }

    private void handleMoneyReward(@NotNull Player player, int reward) {
        Economy economy = Vault.getEconomy();

        if (economy.getBalance(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY.getMessage());
            return;
        }
        economy.withdrawPlayer(player, reward);
    }

    private void handlePlayerPointsReward(@NotNull Player player, int reward) {
        PlayerPointsAPI api = CBounty.getPlayerPointsManager();
        UUID uuid = player.getUniqueId();

        if (api.look(uuid) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_PLAYERPOINTS.getMessage());
            return;
        }

        api.take(uuid, reward);
    }
}
