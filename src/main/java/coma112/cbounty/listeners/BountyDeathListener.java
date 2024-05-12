package coma112.cbounty.listeners;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.MessageKeys;
import coma112.cbounty.event.TargetDeathEvent;
import coma112.cbounty.hooks.vault.Vault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class BountyDeathListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player target = event.getEntity();
        Player killer = target.getKiller();

        if (killer != null) {
            if (CBounty.getDatabaseManager().isBounty(target)) {
                if (killer.equals(CBounty.getDatabaseManager().getSender(target))) {
                    switch (CBounty.getDatabaseManager().getRewardType(target)) {
                        case TOKEN -> {
                             if (CBounty.getInstance().getToken().isEnabled()) CBounty.getTokenManager().addTokens(killer, CBounty.getDatabaseManager().getReward(target));
                             Vault.getEconomy().depositPlayer(killer, CBounty.getDatabaseManager().getReward(target));
                             killer.sendMessage(MessageKeys.FEATURE_DISABLED_EVENT.getMessage());
                        }

                        case MONEY -> Vault.getEconomy().depositPlayer(killer, CBounty.getDatabaseManager().getReward(target));
                    }
                }

                CBounty.getDatabaseManager().removeBounty(target);

                if (!CBounty.getDatabaseManager().isSenderIsRandom(target)) {
                    killer.sendMessage(MessageKeys.BOUNTY_DEAD_KILLER.getMessage());
                }

                target.sendMessage(MessageKeys.BOUNTY_DEAD_TARGET.getMessage());

                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageKeys.BOUNTY_DEAD_EVERYONE
                        .getMessage()
                        .replace("{name}", target.getName())));

                CBounty.getInstance().getServer().getPluginManager().callEvent(new TargetDeathEvent(killer, target,
                        CBounty.getDatabaseManager().getReward(target),
                        CBounty.getDatabaseManager().getRewardType(target)));
            }
        }
    }
}
