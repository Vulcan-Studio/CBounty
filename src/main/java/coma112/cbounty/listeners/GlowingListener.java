package coma112.cbounty.listeners;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.events.CreateBountyEvent;
import coma112.cbounty.events.TargetDeathEvent;
import coma112.cbounty.hooks.Webhook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class GlowingListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) { tryToSetGlowing(event.getPlayer()); }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        tryToSetGlowing(event.getPlayer());
    }

    @EventHandler
    public void onCreate(CreateBountyEvent event) throws IOException {
        tryToSetGlowing(event.getTarget());

        Webhook.sendWebhook(
                replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_DESCRIPTION.getString(), event),
                Webhook.getColor(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_COLOR.getString()),
                replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_AUTHOR_NAME.getString(), event),
                replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_AUTHOR_URL.getString(), event),
                replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_AUTHOR_ICON.getString(), event),
                replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_FOOTER_TEXT.getString(), event),
                replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_FOOTER_ICON.getString(), event),
                replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_THUMBNAIL.getString(), event),
                replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_TITLE.getString(), event),
                replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_IMAGE.getString(), event)
        );
    }

    @EventHandler
    public void onDeath(TargetDeathEvent event) {
        tryToRemoveGlowing(event.getTarget());
    }

    public void tryToSetGlowing(@NotNull Player player) {
        String playerName = player.getName();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        if (isGlowingEnabled() && CBounty.getDatabaseManager().isBounty(player)) {
            player.setGlowing(true);

            Team team = scoreboard.getTeam(playerName);
            if (team != null) team.unregister();
            team = scoreboard.registerNewTeam(playerName);
            team.setColor(ChatColor.valueOf(ConfigKeys.GLOWING_COLOR.getString()));
            team.addEntry(playerName);
        }
    }

    public void tryToRemoveGlowing(@NotNull Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());

        if (team != null) {
            team.removeEntry(player.getName());
            team.setColor(ChatColor.WHITE);
            team.unregister();
            player.setGlowing(false);
        }
    }

    private boolean isGlowingEnabled() {
        return ConfigKeys.GLOWING_ENABLED.getBoolean();
    }

    private String replacePlaceholdersBountyCreate(@NotNull String text, CreateBountyEvent event) {
        return text.replace("{sender}", event.getSender().getName())
                .replace("{target}", event.getTarget().getName())
                .replace("{reward}", String.valueOf(event.getReward()))
                .replace("{rewardType}", String.valueOf(event.getRewardType()));
    }
}
