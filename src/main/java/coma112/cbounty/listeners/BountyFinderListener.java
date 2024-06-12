package coma112.cbounty.listeners;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.item.IItemBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class BountyFinderListener implements Listener {
    @EventHandler
    public void onPlayerItemHeld(final PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (item != null && item.equals(IItemBuilder.createItemFromSection("bountyfinder-item"))) {
            CBounty.getInstance().getScheduler().runTaskTimer(() -> {
                if (player.getInventory().getItemInMainHand().equals(item)) player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(getNearestBountyInfo(player)));
            }, 0, 10);
        }
    }


    private String getNearestBountyInfo(@NotNull Player player) {
        double nearestDistance = Double.MAX_VALUE;
        String nearestBountyName = "";

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (otherPlayer.equals(player)) continue;

            if (CBounty.getDatabaseManager().isBounty(otherPlayer)) {
                double distance = player.getLocation().distance(otherPlayer.getLocation());

                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestBountyName = otherPlayer.getName();
                }
            }
        }

        return nearestBountyName.isEmpty()
                ? ConfigKeys.NO_BOUNTY_NEARBY.getString()
                : ConfigKeys.NEAREST_BOUNTY.getString().replace("{bounty}", nearestBountyName).replace("{distance}", String.valueOf((int) nearestDistance));
    }
}
