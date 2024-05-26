package coma112.cbounty.listeners;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.event.CreateBountyEvent;
import coma112.cbounty.event.TargetDeathEvent;
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

@SuppressWarnings("deprecation")
public class GlowingListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) { tryToSetGlowing(event.getPlayer()); }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        tryToSetGlowing(event.getPlayer());
    }

    @EventHandler
    public void onCreate(CreateBountyEvent event) {
        tryToSetGlowing(event.getTarget());
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
}
