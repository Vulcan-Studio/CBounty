package coma112.cbounty.managers;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.MessageKeys;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
public record Top(@NotNull OfflinePlayer player, int streak) {
    public static TextComponent getTopStreak(int value) {
        List<Top> topStreaks = CBounty.getDatabaseManager().getTop(value);

        TextComponent message = new TextComponent(MessageKeys.TOP_HEADER
                .getMessage()
                .replace("{value}", String.valueOf(value)));

        IntStream
                .range(0, topStreaks.size())
                .forEach(index -> {
            Top top = topStreaks.get(index);

            message.addExtra(MessageKeys.TOP_MESSAGE
                    .getMessage()
                    .replace("{streak}", String.valueOf(top.streak))
                    .replace("{name}", Objects.requireNonNull(top.player().getName()))
                    .replace("{place}", String.valueOf(index + 1)));

            if (index < topStreaks.size() - 1) message.addExtra("\n");
        });

        return message;
    }
}
