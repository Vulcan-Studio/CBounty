package coma112.cbounty.event;

import coma112.cbounty.enums.RewardType;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class CreateBountyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player sender;
    private final Player target;
    private final int reward;
    private final RewardType rewardType;

    public CreateBountyEvent(@NotNull Player sender, @NotNull Player target, int reward, @NotNull RewardType rewardType) {
        this.sender = sender;
        this.target = target;
        this.reward = reward;
        this.rewardType = rewardType;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

