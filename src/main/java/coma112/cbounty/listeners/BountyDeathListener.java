package coma112.cbounty.listeners;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.keys.MessageKeys;
import coma112.cbounty.events.TargetDeathEvent;
import coma112.cbounty.hooks.Webhook;
import coma112.cbounty.hooks.vault.Vault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

public class BountyDeathListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) throws IOException {
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

                if (!CBounty.getDatabaseManager().isSenderIsRandom(target)) killer.sendMessage(MessageKeys.BOUNTY_DEAD_KILLER.getMessage());

                Webhook.sendWebhook(
                        replacePlaceholdersTargetDeath(ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_DESCRIPTION.getString(), killer, target),
                        Webhook.getColor(ConfigKeys.WEBHOOK_BOUNTY_DEATH_EMBED_COLOR.getString()),
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

                CBounty.getInstance().getServer().getPluginManager().callEvent(new TargetDeathEvent(killer, target,
                        CBounty.getDatabaseManager().getReward(target),
                        CBounty.getDatabaseManager().getRewardType(target)));

                CBounty.getDatabaseManager().removeBounty(target);
            }
        }
    }

    private String replacePlaceholdersTargetDeath(@NotNull String text, Player killer, Player target) {
        if (CBounty.getDatabaseManager().isSenderIsRandom(target)) {
            return text.replace("{killer}", ConfigKeys.WEBHOOK_RANDOM_SENDER.getString())
                    .replace("{target}", target.getName())
                    .replace("{reward}", String.valueOf(CBounty.getDatabaseManager().getReward(target)))
                    .replace("{rewardType}", String.valueOf(CBounty.getDatabaseManager().getRewardType(target)));
        }

        return text.replace("{killer}", killer.getName())
                .replace("{target}", target.getName())
                .replace("{reward}", String.valueOf(CBounty.getDatabaseManager().getReward(target)))
                .replace("{rewardType}", String.valueOf(CBounty.getDatabaseManager().getRewardType(target)));
    }
}
