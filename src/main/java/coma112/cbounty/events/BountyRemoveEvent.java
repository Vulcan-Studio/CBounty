package coma112.cbounty.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class BountyRemoveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player sender;
    private final Player target;

    public BountyRemoveEvent(@Nullable Player sender, @NotNull Player target) {
        this.sender = sender;
        this.target = target;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
