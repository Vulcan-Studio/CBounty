package coma112.cbounty.listeners;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.keys.MessageKeys;
import coma112.cbounty.events.BountyDeathEvent;
import coma112.cbounty.hooks.PlayerPoints;
import coma112.cbounty.hooks.Webhook;
import coma112.cbounty.hooks.Vault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.io.IOException;
import static coma112.cbounty.hooks.Webhook.replacePlaceholdersTargetDeath;

public class BountyDeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        Player target = event.getEntity();
        Player killer = target.getKiller();

        if (killer != null) {
            if (CBounty.getDatabaseManager().isBounty(target)) {
                if (!killer.equals(CBounty.getDatabaseManager().getSender(target))) {

                    switch (CBounty.getDatabaseManager().getRewardType(target)) {
                        case TOKEN -> {
                             if (CBounty.getInstance().getToken().isEnabled()) CBounty.getTokenManager().addTokens(killer, CBounty.getDatabaseManager().getReward(target));
                             Vault.getEconomy().depositPlayer(killer, CBounty.getDatabaseManager().getReward(target));
                             killer.sendMessage(MessageKeys.FEATURE_DISABLED_EVENT.getMessage());
                        }

                        case PLAYERPOINTS -> {
                            if (PlayerPoints.isEnabled()) CBounty.getPlayerPointsManager().give(killer.getUniqueId(), CBounty.getDatabaseManager().getReward(target));
                            Vault.getEconomy().depositPlayer(killer, CBounty.getDatabaseManager().getReward(target));
                            killer.sendMessage(MessageKeys.FEATURE_DISABLED_EVENT.getMessage());
                        }

                        case LEVEL -> {
                            if (ConfigKeys.DEPENDENCY_LEVEL.getBoolean()) killer.setLevel(killer.getLevel() + CBounty.getDatabaseManager().getReward(target));
                            Vault.getEconomy().depositPlayer(killer, CBounty.getDatabaseManager().getReward(target));
                            killer.sendMessage(MessageKeys.FEATURE_DISABLED_EVENT.getMessage());
                        }

                        case MONEY -> Vault.getEconomy().depositPlayer(killer, CBounty.getDatabaseManager().getReward(target));
                    }
                }

                if (!CBounty.getDatabaseManager().isSenderIsRandom(target)) killer.sendMessage(MessageKeys.BOUNTY_DEAD_KILLER.getMessage());

                Webhook.sendWebhook(
                        replacePlaceholdersTargetDeath(ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_DESCRIPTION.getString(), killer, target),
                        ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_COLOR.getString(),
                        replacePlaceholdersTargetDeath(ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_AUTHOR_NAME.getString(), killer, target),
                        replacePlaceholdersTargetDeath(ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_AUTHOR_URL.getString(), killer, target),
                        replacePlaceholdersTargetDeath(ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_AUTHOR_ICON.getString(), killer, target),
                        replacePlaceholdersTargetDeath(ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_FOOTER_TEXT.getString(), killer, target),
                        replacePlaceholdersTargetDeath(ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_FOOTER_ICON.getString(), killer, target),
                        replacePlaceholdersTargetDeath(ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_THUMBNAIL.getString(), killer, target),
                        replacePlaceholdersTargetDeath(ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_TITLE.getString(), killer, target),
                        replacePlaceholdersTargetDeath(ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_IMAGE.getString(), killer, target)
                );

                target.sendMessage(MessageKeys.BOUNTY_DEAD_TARGET.getMessage());

                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageKeys.BOUNTY_DEAD_EVERYONE
                        .getMessage()
                        .replace("{name}", target.getName())));

                CBounty.getDatabaseManager().removeBounty(target);

                CBounty.getInstance().getServer().getPluginManager().callEvent(new BountyDeathEvent(killer, target,
                        CBounty.getDatabaseManager().getReward(target),
                        CBounty.getDatabaseManager().getRewardType(target)));
            }
        }
    }
}
