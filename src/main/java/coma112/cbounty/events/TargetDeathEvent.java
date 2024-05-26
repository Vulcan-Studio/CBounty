package coma112.cbounty.events;

import coma112.cbounty.enums.RewardType;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class TargetDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player sender;
    private final Player target;
    private final int reward;
    private final RewardType rewardType;

    public TargetDeathEvent(@Nullable Player sender, @NotNull Player target, int reward, @NotNull RewardType rewardType) {
        this.sender = sender;
        this.target = target;
        this.reward = reward;
        this.rewardType = rewardType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
