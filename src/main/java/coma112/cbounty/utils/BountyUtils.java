package coma112.cbounty.utils;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.MessageKeys;
import coma112.cbounty.hooks.CoinsEngine;
import coma112.cbounty.hooks.Vault;
import coma112.cbounty.processor.MessageProcessor;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class BountyUtils {
    public static void sendActionBar(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(MessageProcessor.process(message)));
    }


    public static boolean hasItem(@NotNull Inventory inventory, @NotNull ItemStack item) {
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.isSimilar(item)) return true;
        }

        return false;
    }

    public static boolean handleTokenReward(@NotNull Player player, int reward) {
        if (CBounty.getInstance().getToken().getTokens(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_TOKEN.getMessage());
            return false;
        } else {
            CBounty.getTokenManager().removeTokens(player, reward);
            return true;
        }
    }

    public static boolean handleMoneyReward(@NotNull Player player, int reward) {
        Economy economy = Vault.getEconomy();
        if (economy.getBalance(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY.getMessage());
            return false;
        } else {
            economy.withdrawPlayer(player, reward);
            return true;
        }
    }

    public static boolean handlePlayerPointsReward(@NotNull Player player, int reward) {
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

    public static boolean handleCoinsEngineReward(@NotNull Player player, int reward) {
        if (CoinsEngineAPI.getBalance(player, CoinsEngine.getCurrency()) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_COINSENGINE.getMessage());
            return false;
        } else {
            CoinsEngineAPI.removeBalance(player, CoinsEngine.getCurrency(), reward);
            return true;
        }
    }

    public static boolean handleLevelReward(@NotNull Player player, int reward) {
        if (player.getLevel() < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_LEVEL.getMessage());
            return false;
        } else {
            player.setLevel(player.getLevel() - reward);
            return true;
        }
    }
}
