package coma112.cbounty.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import coma112.cbounty.CBounty;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.events.BountyCreateEvent;
import coma112.cbounty.managers.Bounty;
import coma112.cbounty.managers.Top;
import coma112.cbounty.utils.BountyLogger;
import com.mongodb.client.*;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

@Getter
public class MongoDB extends AbstractDatabase {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> bountyCollection;
    private final MongoCollection<Document> counterCollection;

    public MongoDB(@NotNull ConfigurationSection section) {
        String host = section.getString("host");
        int port = section.getInt("port");
        String databaseName = section.getString("database");

        String username = section.getString("username");
        String password = section.getString("password");
        String connectionString;

        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) connectionString = String.format("mongodb://%s:%s@%s:%d/%s", username, password, host, port, databaseName);
        else connectionString = String.format("mongodb://%s:%d/%s", host, port, databaseName);


        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(Objects.requireNonNull(databaseName));
        bountyCollection = database.getCollection("bounty");
        counterCollection = database.getCollection("counter");
    }

    public void initializeCounter() {
        if (counterCollection.find(eq("_id", "bountyId")).first() == null) counterCollection.insertOne(new Document("_id", "bountyId").append("seq", 0));
    }

    private int getNextId() {
        Document result = counterCollection.findOneAndUpdate(
                eq("_id", "bountyId"),
                Updates.inc("seq", 1),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );

        return Objects.requireNonNull(result).getInteger("seq");
    }

    public void createCollection() {
        database.createCollection("bounty");
    }

    @Override
    public void createBounty(@NotNull Player player, @NotNull Player target, @NotNull RewardType rewardType, int reward) {
        Document bounty = new Document("_id", getNextId())
                .append("player", player.getName())
                .append("target", target.getName())
                .append("rewardType", rewardType.name())
                .append("reward", reward)
                .append("bountyDate", new java.util.Date())
                .append("streak", 0);

        bountyCollection.insertOne(bounty);
        CBounty.getInstance().getServer().getPluginManager().callEvent(new BountyCreateEvent(player, target, reward, rewardType));
    }

    @Override
    public void createRandomBounty(@NotNull Player target, @NotNull RewardType rewardType, int reward) {
        Document bounty = new Document("_id", getNextId())
                .append("player", ConfigKeys.RANDOM_BOUNTY_PLAYER_VALUE.getString())
                .append("target", target.getName())
                .append("rewardType", rewardType.name())
                .append("reward", reward)
                .append("bountyDate", new java.util.Date())
                .append("streak", 0);

        bountyCollection.insertOne(bounty);
        CBounty.getInstance().getServer().getPluginManager().callEvent(new BountyCreateEvent(null, target, reward, rewardType));
    }

    @Override
    public boolean isSenderIsRandom(@NotNull Player player) {
        Document result = bountyCollection.find(eq("target", player.getName())).first();
        return result != null && result.getString("player").equals(ConfigKeys.RANDOM_BOUNTY_PLAYER_VALUE.getString());
    }

    @Override
    public List<Bounty> getBounties() {
        List<Bounty> bounties = new ArrayList<>();
        try (MongoCursor<Document> cursor = bountyCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                bounties.add(new Bounty(
                        doc.getInteger("_id"),
                        doc.getString("player"),
                        doc.getString("target"),
                        RewardType.valueOf(doc.getString("rewardType")),
                        doc.getInteger("reward")
                ));
            }
        }
        return bounties;
    }

    @Override
    public int getStreak(@NotNull OfflinePlayer player) {
        bountyCollection.updateOne(eq("target", player.getName()), set("streak", getDaysDifference(player)));
        Document result = bountyCollection.find(eq("target", player.getName())).first();
        return result != null ? result.getInteger("streak", 0) : 0;
    }

    private int getDaysDifference(@NotNull OfflinePlayer player) {
        Document result = bountyCollection.find(eq("target", player.getName()))
                .sort(new Document("bountyDate", -1))
                .first();

        if (result != null) {
            long diff = new java.util.Date().getTime() - result.getDate("bountyDate").getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        }

        return 0;
    }

    @Override
    public boolean isBounty(@NotNull Player player) {
        return bountyCollection.find(eq("target", player.getName())).first() != null;
    }

    @Override
    public void changeReward(@NotNull Player player, int newReward) {
        bountyCollection.updateOne(eq("target", player.getName()), inc("reward", newReward));
    }

    @Override
    public int getReward(@NotNull Player player) {
        Document result = bountyCollection.find(eq("target", player.getName())).first();
        return result != null ? result.getInteger("reward", 0) : 0;
    }

    @Override
    public RewardType getRewardType(@NotNull Player player) {
        Document result = bountyCollection.find(eq("target", player.getName())).first();
        return result != null ? RewardType.valueOf(result.getString("rewardType")) : RewardType.MONEY;
    }

    @Override
    public Player getSender(@NotNull Player player) {
        Document result = bountyCollection.find(eq("target", player.getName())).first();
        return result != null ? Bukkit.getPlayerExact(result.getString("player")) : null;
    }

    @Override
    public void removeBounty(@NotNull Player player) {
        bountyCollection.deleteOne(eq("target", player.getName()));
    }

    @Override
    public List<Top> getTop(int number) {
        List<Top> topStreaks = new ArrayList<>();

        try (MongoCursor<Document> cursor = bountyCollection.find()
                .sort(new Document("streak", -1))
                .limit(number)
                .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                topStreaks.add(new Top(
                        Bukkit.getOfflinePlayer(doc.getString("target")),
                        doc.getInteger("streak")
                ));
            }
        }
        return topStreaks;
    }

    @Override
    public String getTopStreakPlayer(int top) {
        Document result = bountyCollection.find()
                .sort(new Document("streak", -1))
                .skip(top - 1)
                .first();

        return result != null ? result.getString("target") : null;
    }

    @Override
    public int getTopStreak(int top) {
        Document result = bountyCollection.find()
                .sort(new Document("streak", -1))
                .skip(top - 1)
                .first();

        return result != null ? result.getInteger("streak") : 0;
    }

    @Override
    public void reconnect(@NotNull ConfigurationSection section) {
        disconnect();
        new MongoDB(Objects.requireNonNull(CBounty.getInstance().getConfiguration().getSection("database.mongodb")));
        initializeCounter();
    }

    @Override
    public boolean reachedMaximumBounty(@NotNull Player player) {
        long count = bountyCollection.countDocuments(eq("player", player.getName()));
        return count >= ConfigKeys.MAXIMUM_BOUNTY.getInt();
    }

    @Override
    public boolean isConnected() {
        try {
            mongoClient.getDatabase("admin").runCommand(new Document("ping", 1));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public void disconnect() {
        if (mongoClient != null) mongoClient.close();
    }
}

