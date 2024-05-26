package coma112.cbounty.processor;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.events.CreateBountyEvent;
import coma112.cbounty.hooks.Webhook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
                Bukkit.getServer().getScheduler().runTask(CBounty.getInstance(), () -> {
                    try {
                        addRandomBounty();
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }
                });
            }
        }, 0, ConfigKeys.RANDOM_BOUNTY_PER_SECOND.getInt() * 1000L);
    }

    private void addRandomBounty() throws IOException {
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

                Webhook.sendWebhook(
                        replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_DESCRIPTION.getString(), randomPlayer),
                        Webhook.getColor(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_COLOR.getString()),
                        replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_AUTHOR_NAME.getString(), randomPlayer),
                        replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_AUTHOR_URL.getString(), randomPlayer),
                        replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_AUTHOR_ICON.getString(), randomPlayer),
                        replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_FOOTER_TEXT.getString(), randomPlayer),
                        replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_FOOTER_ICON.getString(), randomPlayer),
                        replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_THUMBNAIL.getString(), randomPlayer),
                        replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_TITLE.getString(), randomPlayer),
                        replacePlaceholdersBountyCreate(ConfigKeys.WEBHOOK_BOUNTY_CREATE_EMBED_IMAGE.getString(), randomPlayer)
                );
            }
        }
    }

    private String replacePlaceholdersBountyCreate(@NotNull String text, @NotNull Player target) {
        return text.replace("{sender}", ConfigKeys.WEBHOOK_RANDOM_SENDER.getString())
                .replace("{target}", target.getName())
                .replace("{reward}", String.valueOf(CBounty.getDatabaseManager().getReward(target)))
                .replace("{rewardType}", String.valueOf(CBounty.getDatabaseManager().getRewardType(target)));
    }
}
