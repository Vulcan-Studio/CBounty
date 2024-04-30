package coma112.cbounty.interfaces;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface IBounty {
    void tryToSetBountyWithToken(@NotNull Player player, int reward);

    void tryToSetBountyWithVault(@NotNull Player player, int reward);
}
