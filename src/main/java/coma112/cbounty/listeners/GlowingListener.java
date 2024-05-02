package coma112.cbounty.listeners;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.event.CreateBountyEvent;
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
    public void onJoin(PlayerJoinEvent event) {
        tryToSetGlowing(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        tryToSetGlowing(event.getPlayer());
    }

    @EventHandler
    public void onCreate(CreateBountyEvent event) {
        tryToSetGlowing(event.getTarget());
    }

    public void tryToSetGlowing(@NotNull Player player) {
        String playerName = player.getName();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        if (isGlowingEnabled()) {
            if (!isGlowingColorEnabled()) {
                player.setGlowing(CBounty.getDatabaseManager().isBounty(player));
            }
        }

        if (isGlowingEnabled()) {
            if (isGlowingColorEnabled()) {
                Team team = scoreboard.registerNewTeam(playerName);
                team.setColor(ChatColor.valueOf(ConfigKeys.GLOWING_COLOR.getString()));
                team.addEntry(playerName);
                player.setGlowing(CBounty.getDatabaseManager().isBounty(player));
            }
        }
    }

    private boolean isGlowingEnabled() {
        return ConfigKeys.GLOWING_ENABLED.getBoolean();
    }

    private boolean isGlowingColorEnabled() {
        return ConfigKeys.GLOWING_COLOR_ENABLED.getBoolean();
    }
}
