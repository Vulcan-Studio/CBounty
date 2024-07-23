package coma112.cbounty.database;

import coma112.cbounty.enums.RewardType;
import coma112.cbounty.managers.Bounty;
import coma112.cbounty.managers.Top;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractDatabase {
    // Bounty Operations
    public abstract void createBounty(@NotNull Player player, @NotNull Player target, @NotNull RewardType rewardType, int reward);
    public abstract void createRandomBounty(@NotNull Player target, @NotNull RewardType rewardType, int reward);
    public abstract void changeReward(@NotNull Player player, int newReward);

    public abstract List<Bounty> getBounties();
    public abstract Player getSender(@NotNull Player player);
    public abstract void removeBounty(@NotNull Player player);

    // Streak and Top Operations
    public abstract int getStreak(@NotNull OfflinePlayer player);
    public abstract List<Top> getTop(int number);
    public abstract String getTopStreakPlayer(int top);
    public abstract int getTopStreak(int top);

    // Reward Getter
    public abstract int getReward(@NotNull Player player);
    public abstract RewardType getRewardType(@NotNull Player player);

    // Connection Management
    public abstract void reconnect();
    public abstract boolean isConnected();
    public abstract void disconnect();

    // Booleans
    public abstract boolean isBounty(@NotNull Player player);
    public abstract boolean reachedMaximumBounty(@NotNull Player player);
    public abstract boolean isSenderIsRandom(@NotNull Player player);
}
