package coma112.cbounty.processor;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.enums.keys.ConfigKeys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BountyScheduler {
    private final Timer timer;

    public BountyScheduler() {
        this.timer = new Timer();
    }

    public void startScheduling() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Bukkit.getServer().getScheduler().runTask(CBounty.getInstance(), () -> addRandomBounty());
            }
        }, 0, ConfigKeys.RANDOM_BOUNTY_PER_SECOND.getInt() * 1000L);
    }

    private void addRandomBounty() {
        Player[] onlinePlayers = Bukkit.getServer().getOnlinePlayers().toArray(new Player[0]);

        if (onlinePlayers.length > 0) {
            List<Player> playersWithoutBounty = Arrays.stream(onlinePlayers)
                    .filter(player -> !CBounty.getDatabaseManager().isBounty(player))
                    .toList();

            if (!playersWithoutBounty.isEmpty()) {
                Player randomPlayer = playersWithoutBounty.get((int) (Math.random() * playersWithoutBounty.size()));
                CBounty.getDatabaseManager().createRandomBounty(
                        randomPlayer,
                        RewardType.valueOf(ConfigKeys.RANDOM_BOUNTY_REWARDTYPE.getString()),
                        ConfigKeys.RANDOM_BOUNTY_REWARD.getInt());
            }
        }
    }
}
