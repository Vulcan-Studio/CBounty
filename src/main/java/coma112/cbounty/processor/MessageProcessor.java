package coma112.cbounty.processor;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class MessageProcessor {
    public static @NotNull String process(@Nullable String message) {
        if (message == null) return "";

        Pattern pattern = Pattern.compile("#[a-fA-F\\d]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());

            String result = hexCode
                    .substring(1)
                    .chars()
                    .mapToObj(c -> "&" + (char) c)
                    .collect(Collectors.joining());

            message = message.replace(hexCode, result);
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}

