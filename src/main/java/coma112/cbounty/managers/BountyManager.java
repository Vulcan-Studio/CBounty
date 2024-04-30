package coma112.cbounty.managers;

import coma112.cbounty.hooks.Vault;
import coma112.cbounty.interfaces.IBounty;
import coma112.cbounty.language.MessageKeys;
import net.coma.ctoken.CToken;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BountyManager implements IBounty {
    @Override
    public void tryToSetBountyWithToken(@NotNull Player player, int reward) {
        if (CToken.getInstance().getDatabaseManager().getBalance(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_TOKEN);
            return;
        }


        if (CToken.getInstance().getDatabaseManager().getBalance(player) >= reward) CToken.getInstance().getDatabaseManager().takeFromBalance(player, reward);
    }

    @Override
    public void tryToSetBountyWithVault(@NotNull Player player, int reward) {
        if (Vault.getEconomy().getBalance(player) < reward) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_MONEY);
            return;
        }

        if (Vault.getEconomy().getBalance(player) >= reward) Vault.getEconomy().depositPlayer(player, reward);
    }
}
