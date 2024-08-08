package coma112.cbounty.processor;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.hooks.Webhook;
import coma112.cbounty.utils.BountyLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BountyScheduler {
    public void startScheduling() {
        if (ConfigKeys.RANDOM_BOUNTY_ENABLED.getBoolean()) {
            CBounty.getInstance().getScheduler().runTaskTimer(() -> {
                try {
                    addRandomBounty();
                } catch (IOException | NoSuchFieldException | IllegalAccessException exception) {
                    BountyLogger.error(exception.getMessage());
                }
            }, 0, ConfigKeys.RANDOM_BOUNTY_PER_SECOND.getInt() * 20L);
        }
    }

    private void addRandomBounty() throws IOException, NoSuchFieldException, IllegalAccessException {
        Player[] onlinePlayers = Bukkit.getServer().getOnlinePlayers().toArray(new Player[0]);

        if (onlinePlayers.length > 0) {
            List<Player> playersWithoutBounty = Arrays
                    .stream(onlinePlayers)
                    .filter(player -> !CBounty.getDatabaseManager().isBounty(player))
                    .toList();

            if (!playersWithoutBounty.isEmpty()) CBounty.getDatabaseManager().createRandomBounty(playersWithoutBounty.get((int) (Math.random() * playersWithoutBounty.size())), RewardType.valueOf(ConfigKeys.RANDOM_BOUNTY_REWARDTYPE.getString()), ConfigKeys.RANDOM_BOUNTY_REWARD.getInt());
        }
    }
}
