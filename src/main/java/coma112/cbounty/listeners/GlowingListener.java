package coma112.cbounty.listeners;

import coma112.cbounty.CBounty;
import coma112.cbounty.event.CreateBountyEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GlowingListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setGlowing(CBounty.getDatabaseManager().isBounty(player));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        player.setGlowing(CBounty.getDatabaseManager().isBounty(player));
    }

    @EventHandler
    public void onCreate(CreateBountyEvent event) {
        event.getTarget().setGlowing(true);
    }
}
