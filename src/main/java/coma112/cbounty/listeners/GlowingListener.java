package coma112.cbounty.listeners;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.events.BountyCreateEvent;
import coma112.cbounty.events.BountyDeathEvent;
import coma112.cbounty.events.BountyRemoveEvent;
import coma112.cbounty.hooks.Webhook;
import coma112.cbounty.utils.StartingUtils;
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

import java.io.IOException;

import static coma112.cbounty.hooks.Webhook.replacePlaceholdersBountyCreate;
import static coma112.cbounty.hooks.Webhook.replacePlaceholdersBountyRemove;

@SuppressWarnings("deprecation")
public class GlowingListener implements Listener {
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        tryToSetGlowing(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        tryToSetGlowing(event.getPlayer());
    }

    @EventHandler
    public void onDeath(final BountyDeathEvent event) {
        tryToRemoveGlowing(event.getTarget());
    }

    @EventHandler
    public void onRemove(final BountyRemoveEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        tryToRemoveGlowing(event.getTarget());

        Webhook.sendWebhook(
                replacePlaceholdersBountyRemove(ConfigKeys.WEBHOOK_BOUNTY_REMOVE_EMBED_DESCRIPTION.getString(), event),
                ConfigKeys.WEBHOOK_BOUNTY_REMOVE_EMBED_COLOR.getString(),
                replacePlaceholdersBountyRemove(ConfigKeys.WEBHOOK_BOUNTY_REMOVE_EMBED_AUTHOR_NAME.getString(), event),
                replacePlaceholdersBountyRemove(ConfigKeys.WEBHOOK_BOUNTY_REMOVE_EMBED_AUTHOR_URL.getString(), event),
                replacePlaceholdersBountyRemove(ConfigKeys.WEBHOOK_BOUNTY_REMOVE_EMBED_AUTHOR_ICON.getString(), event),
                replacePlaceholdersBountyRemove(ConfigKeys.WEBHOOK_BOUNTY_REMOVE_EMBED_FOOTER_TEXT.getString(), event),
                replacePlaceholdersBountyRemove(ConfigKeys.WEBHOOK_BOUNTY_REMOVE_EMBED_FOOTER_ICON.getString(), event),
                replacePlaceholdersBountyRemove(ConfigKeys.WEBHOOK_BOUNTY_REMOVE_EMBED_THUMBNAIL.getString(), event),
                replacePlaceholdersBountyRemove(ConfigKeys.WEBHOOK_BOUNTY_REMOVE_EMBED_TITLE.getString(), event),
                replacePlaceholdersBountyRemove(ConfigKeys.WEBHOOK_BOUNTY_REMOVE_EMBED_IMAGE.getString(), event)
        );
    }

    @EventHandler
    public void onCreate(final BountyCreateEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        if (!StartingUtils.isFolia) tryToSetGlowing(event.getTarget());

        Webhook.sendWebhook(
                replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_DESCRIPTION.getString(), event),
                ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_COLOR.getString(),
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

    public void tryToSetGlowing(@NotNull Player player) {
        String playerName = player.getName();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        if (isEnabled() && CBounty.getDatabaseManager().isBounty(player) && !StartingUtils.isFolia) {
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

        if (team != null && !StartingUtils.isFolia) {
            team.removeEntry(player.getName());
            team.unregister();
            player.setGlowing(false);
        }
    }

    private boolean isEnabled() {
        return ConfigKeys.GLOWING_ENABLED.getBoolean();
    }
}
