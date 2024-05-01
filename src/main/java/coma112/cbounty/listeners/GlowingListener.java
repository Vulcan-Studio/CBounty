package coma112.cbounty.listeners;

import coma112.cbounty.CBounty;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GlowingListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (CBounty.getDatabaseManager().isBounty(player)) player.setGlowing(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (CBounty.getDatabaseManager().isBounty(player)) player.setGlowing(true);
    }
}
