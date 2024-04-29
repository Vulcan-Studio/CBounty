package coma112.cbounty.managers;

import coma112.cbounty.enums.RewardType;
import org.jetbrains.annotations.NotNull;

public record Bounty(int id, @NotNull String player, @NotNull String target, @NotNull RewardType reward_type, int reward) {}
