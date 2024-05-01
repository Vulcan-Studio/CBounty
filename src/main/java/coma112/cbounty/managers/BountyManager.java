package coma112.cbounty.managers;

import coma112.cbounty.CBounty;
import coma112.cbounty.hooks.Vault;
import coma112.cbounty.enums.keys.MessageKeys;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalLong;

public class BountyManager {
    public void tryToSetBountyWithToken(@NotNull Player player, int reward) {
        if (getTokens(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_TOKEN.getMessage());
            return;
        }

        if (getTokens(player) >= reward) CBounty.getTokenManager().removeTokens(player, reward);
    }

    public void tryToSetBountyWithVault(@NotNull Player player, int reward) {
        if (Vault.getEconomy().getBalance(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY.getMessage());
            return;
        }

        if (Vault.getEconomy().getBalance(player) >= reward) Vault.getEconomy().depositPlayer(player, reward);
    }

    public int getTokens(Player player) {
        OptionalLong tokensOptional =  CBounty.getTokenManager().getTokens(player);
        return tokensOptional.isPresent() ? (int) tokensOptional.getAsLong() : 0;
    }
}
