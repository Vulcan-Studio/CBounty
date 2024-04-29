package coma112.cbounty.database;

import coma112.cbounty.enums.RewardType;
import coma112.cbounty.managers.Bounty;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractDatabase {
    public abstract void createBounty(@NotNull Player player, @NotNull Player target, @NotNull RewardType rewardType, int reward);

    public abstract List<Bounty> getBounties();

    public abstract int getStreak(@NotNull OfflinePlayer player);

    public abstract boolean isBounty(@NotNull Player player);

    public abstract int getReward(@NotNull Player player);

    public abstract RewardType getRewardType(@NotNull Player player);

    public abstract Player getSender(@NotNull Player player);

    public abstract void removeBounty(@NotNull Player player);

    public abstract void reconnect(@NotNull ConfigurationSection section);

    public abstract boolean isConnected();

    public abstract void disconnect();
}
