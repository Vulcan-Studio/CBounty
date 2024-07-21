package coma112.cbounty.commands;

import coma112.cbounty.CBounty;
import coma112.cbounty.database.AbstractDatabase;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.keys.MessageKeys;
import coma112.cbounty.events.BountyRemoveEvent;
import coma112.cbounty.hooks.CoinsEngine;
import coma112.cbounty.hooks.Vault;
import coma112.cbounty.item.IItemBuilder;
import coma112.cbounty.managers.Top;
import coma112.cbounty.menu.menus.BountiesMenu;
import coma112.cbounty.processor.MessageProcessor;
import coma112.cbounty.utils.MenuUtils;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import su.nightexpress.coinsengine.api.CoinsEngineAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Command({"bounty", "cbounty"})
public class CommandBounty {
    private final Map<UUID, Long> bountyFinderCooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 5 * 60 * 1000;



    @DefaultFor({"bounty", "cbounty"})
    public void defaultCommand(@NotNull CommandSender sender) {
            help(sender);
    }


    @Subcommand("help")
    public void help(@NotNull CommandSender sender) {
        List<String> helpMessages = MessageKeys.HELP.getMessages();

        for (String message : helpMessages) {
            sender.sendMessage(message);
        }
    }

    @Subcommand("reload")
    @CommandPermission("cbounty.reload")
    public void reload(@NotNull CommandSender sender) {
        CBounty.getInstance().getLanguage().reload();
        CBounty.getInstance().getConfiguration().reload();
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("streaktop")
    @CommandPermission("cbounty.streaktop")
    public void streaktop(@NotNull CommandSender sender, @Default("5") int value) {
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

        // Get the top streaks text
        sender.sendMessage(Top.getTopStreak(value).toPlainText());
    }

    @Subcommand("menu")
    @CommandPermission("cbounty.menu")
    public void menu(@NotNull Player player) {
        new BountiesMenu(MenuUtils.getMenuUtils(player)).open();
    }

    @Subcommand("set")
    @CommandPermission("cbounty.set")
    public void set(@NotNull Player player, @NotNull Player target, RewardType rewardType, int reward) {
        AbstractDatabase databaseManager = CBounty.getDatabaseManager();

        if (!target.isOnline()) {
            player.sendMessage(MessageKeys.PLAYER_NOT_FOUND.getMessage());
            return;
        }

        if (target == player) {
            player.sendMessage(MessageKeys.CANT_BE_YOURSELF.getMessage());
            return;
        }

        if (databaseManager.isBounty(target)) {
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

        if (databaseManager.reachedMaximumBounty(player)) {
            player.sendMessage(MessageKeys.MAX_BOUNTY.getMessage());
            return;
        }

        boolean success = false;
        switch (rewardType) {
            case TOKEN -> success = handleTokenReward(player, reward);
            case MONEY -> success = handleMoneyReward(player, reward);
            case PLAYERPOINTS -> success = handlePlayerPointsReward(player, reward);
            case LEVEL -> success = handleLevelReward(player, reward);
            case COINSENGINE -> success = handleCoinsEngineReward(player, reward);
        }

        if (success) {
            databaseManager.createBounty(player, target, rewardType, reward);
            player.sendMessage(MessageKeys.SUCCESSFUL_SET.getMessage());
            String bountySetMessage = MessageKeys.BOUNTY_SET.getMessage()
                    .replace("{target}", target.getName())
                    .replace("{reward}", String.valueOf(reward))
                    .replace("{rewardType}", rewardType.toString());
            Bukkit.broadcastMessage(bountySetMessage);
        }
    }

    @Subcommand("remove")
    @CommandPermission("cbounty.remove")
    public void remove(@NotNull Player player, @NotNull Player target) {
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
    @CommandPermission("cbounty.raise")
    public void raise(@NotNull Player player, @NotNull Player target, int newReward) {
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
                .replace("{new}", String.valueOf(newReward)));

        // Broadcast the bounty raise message
        String raiseMessage = MessageKeys.BOUNTY_RAISE.getMessage()
                .replace("{target}", target.getName())
                .replace("{oldReward}", String.valueOf(oldReward))
                .replace("{newReward}", String.valueOf(newReward));
        Bukkit.broadcastMessage(raiseMessage);
    }

    @Subcommand("takeoff")
    @CommandPermission("cbounty.takeoff")
    public void takeOff(@NotNull Player player, @NotNull Player target) {
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
            case LEVEL -> player.setLevel(player.getLevel() + CBounty.getDatabaseManager().getReward(target));
            case COINSENGINE -> CoinsEngineAPI.addBalance(player, CoinsEngine.getCurrency(), CBounty.getDatabaseManager().getReward(target));
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

    @Subcommand("bountyfinder")
    @CommandPermission("cbounty.bountyfinder")
    public void giveBountyFinder(@NotNull Player player, @NotNull @Default("me") Player target) {
        if (!target.isOnline()) {
            player.sendMessage(MessageKeys.PLAYER_NOT_FOUND.getMessage());
            return;
        }

        if (target.equals(player)) {
            if (target.getInventory().firstEmpty() == -1) {
                player.sendMessage(MessageKeys.FULL_INVENTORY.getMessage());
                return;
            }

            target.getInventory().addItem(IItemBuilder.createItemFromSection("bountyfinder-item"));
            target.sendMessage(MessageKeys.BOUNTY_FINDER_RECEIVED.getMessage());
            return;
        }

        if (!player.hasPermission("cbounty.bountyfinder.other")) {
            long currentTime = System.currentTimeMillis();
            UUID playerId = player.getUniqueId();

            // Check cooldown
            if (bountyFinderCooldowns.containsKey(playerId)) {
                long lastRequestTime = bountyFinderCooldowns.get(playerId);
                if (currentTime - lastRequestTime < COOLDOWN_TIME) {
                    long timeLeft = (COOLDOWN_TIME - (currentTime - lastRequestTime)) / 1000;
                    player.sendMessage(MessageKeys.BOUNTY_FINDER_COOLDOWN_MESSAGE.getMessage()
                            .replace("{time}", String.valueOf(timeLeft)));
                    return;
                }
            }

            // Update the last request time
            bountyFinderCooldowns.put(playerId, currentTime);
        }

        if (target.getInventory().firstEmpty() == -1) {
            player.sendMessage(MessageKeys.FULL_INVENTORY.getMessage());
            return;
        }

        ItemStack bountyFinderItem = IItemBuilder.createItemFromSection("bountyfinder-item");

        if (hasItem(target.getInventory(), bountyFinderItem)) {
            player.sendMessage(MessageKeys.ITEM_ALREADY_IN_INVENTORY.getMessage());
            return;
        }

        target.getInventory().addItem(bountyFinderItem);
        player.sendMessage(MessageKeys.BOUNTY_FINDER_GIVEN.getMessage().replace("{target}", target.getName()));
        target.sendMessage(MessageKeys.BOUNTY_FINDER_RECEIVED.getMessage());
    }
    private boolean hasItem(Inventory inventory, ItemStack item) {
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.isSimilar(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean handleTokenReward(@NotNull Player player, int reward) {
        if (CBounty.getInstance().getToken().getTokens(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_TOKEN.getMessage());
            return false;
        } else {
            CBounty.getTokenManager().removeTokens(player, reward);
            return true;
        }
    }

    private boolean handleMoneyReward(@NotNull Player player, int reward) {
        Economy economy = Vault.getEconomy();
        if (economy.getBalance(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY.getMessage());
            return false;
        } else {
            economy.withdrawPlayer(player, reward);
            return true;
        }
    }

    private boolean handlePlayerPointsReward(@NotNull Player player, int reward) {
        PlayerPointsAPI api = CBounty.getPlayerPointsManager();
        UUID uuid = player.getUniqueId();
        if (api.look(uuid) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_PLAYERPOINTS.getMessage());
            return false;
        } else {
            api.take(uuid, reward);
            return true;
        }
    }

    private boolean handleCoinsEngineReward(@NotNull Player player, int reward) {
        if (CoinsEngineAPI.getBalance(player, CoinsEngine.getCurrency()) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_COINSENGINE.getMessage());
            return false;
        } else {
            CoinsEngineAPI.removeBalance(player, CoinsEngine.getCurrency(), reward);
            return true;
        }
    }

    private boolean handleLevelReward(@NotNull Player player, int reward) {
        if (player.getLevel() < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_LEVEL.getMessage());
            return false;
        } else {
            player.setLevel(player.getLevel() - reward);
            return true;
        }
    }
}