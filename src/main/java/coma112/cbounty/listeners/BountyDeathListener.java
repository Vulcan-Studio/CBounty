package coma112.cbounty.listeners;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.keys.MessageKeys;
import coma112.cbounty.events.BountyDeathEvent;
import coma112.cbounty.hooks.*;
import coma112.cbounty.utils.BountyUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;

import java.io.IOException;
import java.net.URISyntaxException;

public class BountyDeathListener implements Listener {

    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        Player target = event.getEntity();
        Player killer = target.getKiller();

        if (killer != null) {
            if (CBounty.getDatabaseManager().isBounty(target)) {
                if (killer == CBounty.getDatabaseManager().getSender(target)) return;

                switch (CBounty.getDatabaseManager().getRewardType(target)) {
                    case TOKEN -> {
                        if (Token.isEnabled()) CBounty.getTokenManager().addTokens(killer, CBounty.getDatabaseManager().getReward(target));
                        else {
                            Vault.getEconomy().depositPlayer(killer, CBounty.getDatabaseManager().getReward(target));
                            killer.sendMessage(MessageKeys.FEATURE_DISABLED_EVENT.getMessage());
                        }
                    }

                    case PLAYERPOINTS -> {
                        if (PlayerPoints.isEnabled()) CBounty.getPlayerPointsManager().give(killer.getUniqueId(), CBounty.getDatabaseManager().getReward(target));
                        else {
                            Vault.getEconomy().depositPlayer(killer, CBounty.getDatabaseManager().getReward(target));
                            killer.sendMessage(MessageKeys.FEATURE_DISABLED_EVENT.getMessage());
                        }
                    }

                    case LEVEL -> {
                        if (ConfigKeys.DEPENDENCY_LEVEL.getBoolean()) killer.setLevel(killer.getLevel() + CBounty.getDatabaseManager().getReward(target));
                        else {
                            Vault.getEconomy().depositPlayer(killer, CBounty.getDatabaseManager().getReward(target));
                            killer.sendMessage(MessageKeys.FEATURE_DISABLED_EVENT.getMessage());
                        }
                    }

                    case COINSENGINE -> {
                        if (ConfigKeys.DEPENDENCY_COINSENGINE.getBoolean()) CoinsEngineAPI.addBalance(killer, CoinsEngine.getCurrency(), CBounty.getDatabaseManager().getReward(target));
                        else {
                            Vault.getEconomy().depositPlayer(killer, CBounty.getDatabaseManager().getReward(target));
                            killer.sendMessage(MessageKeys.FEATURE_DISABLED_EVENT.getMessage());
                        }
                    }

                    case MONEY -> Vault.getEconomy().depositPlayer(killer, CBounty.getDatabaseManager().getReward(target));
                }


                if (!CBounty.getDatabaseManager().isSenderIsRandom(target)) killer.sendMessage(MessageKeys.BOUNTY_DEAD_KILLER.getMessage());

                target.sendMessage(MessageKeys.BOUNTY_DEAD_TARGET.getMessage());

                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageKeys.BOUNTY_DEAD_EVERYONE
                        .getMessage()
                        .replace("{name}", target.getName())));

                BountyUtils.setBountyOnDeath(killer, target);
                CBounty.getDatabaseManager().removeBounty(target);

                CBounty.getInstance().getServer().getPluginManager().callEvent(new BountyDeathEvent(killer, target,
                        CBounty.getDatabaseManager().getReward(target),
                        CBounty.getDatabaseManager().getRewardType(target), killer));
            }
        }
    }

    @EventHandler
    public void onDeath(final BountyDeathEvent event) throws IOException, URISyntaxException {
        Webhook.sendWebhookFromString("webhook.bounty-death-embed", event);
    }
}
