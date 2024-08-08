package coma112.cbounty.listeners;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.events.BountyCreateEvent;
import coma112.cbounty.events.BountyDeathEvent;
import coma112.cbounty.events.BountyRemoveEvent;
import coma112.cbounty.hooks.Webhook;
import coma112.cbounty.utils.BountyUtils;
import coma112.cbounty.utils.StartingUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;

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
    public void onRemove(final BountyRemoveEvent event) throws IOException, URISyntaxException {
        tryToRemoveGlowing(event.getTarget());

        Webhook.sendWebhookFromString("webhook.bounty-remove-embed", event);
    }

    @EventHandler
    public void onCreate(final BountyCreateEvent event) throws IOException, URISyntaxException {
        if (!StartingUtils.isFolia) tryToSetGlowing(event.getTarget());

        Webhook.sendWebhookFromString("webhook.bounty-create-embed", event);
    }

    public void tryToSetGlowing(@NotNull Player player) {
        String playerName = player.getName();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        if (isEnabled() && CBounty.getDatabaseManager().isBounty(player) && !StartingUtils.isFolia) {
            Team team = scoreboard.getTeam("cbounty_" + playerName);

            if (team != null) team.unregister();
            else {
                player.setGlowing(true);

                team = scoreboard.registerNewTeam("cbounty_" + playerName);

                team.color(BountyUtils.getNamedTextColor(ConfigKeys.GLOWING_COLOR.getString()));
                team.addPlayer(player);
            }
        }
    }

    public void tryToRemoveGlowing(@NotNull Player player) {
        String playerName = player.getName();
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("cbounty_" + playerName);

        player.setGlowing(false);

        if (team != null && !StartingUtils.isFolia) {
            team.removePlayer(player);
            team.unregister();
            player.setGlowing(false);
        }
    }

    private boolean isEnabled() {
        return ConfigKeys.GLOWING_ENABLED.getBoolean();
    }
}
