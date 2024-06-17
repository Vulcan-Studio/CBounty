package coma112.cbounty.database;

import coma112.cbounty.CBounty;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.events.BountyCreateEvent;
import coma112.cbounty.managers.Bounty;
import coma112.cbounty.managers.Top;
import coma112.cbounty.utils.BountyLogger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class SQLite extends AbstractDatabase {
    private final Connection connection;

    public SQLite() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        File dataFolder = new File(CBounty.getInstance().getDataFolder(), "bounty.db");
        String url = "jdbc:sqlite:" + dataFolder;
        connection = DriverManager.getConnection(url);
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS bounty (ID INT AUTO_INCREMENT PRIMARY KEY, PLAYER VARCHAR(255) NOT NULL, TARGET VARCHAR(255) NOT NULL, REWARD_TYPE VARCHAR(255) NOT NULL, REWARD INT, BOUNTY_DATE DATETIME, STREAK INT DEFAULT 0)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }
    }

    @Override
    public void createBounty(@NotNull Player player, @NotNull Player target, @NotNull RewardType rewardType, int reward) {
        String query = "INSERT INTO bounty (PLAYER, TARGET, REWARD_TYPE, REWARD, BOUNTY_DATE) VALUES (?, ?, ?, ?, DATETIME('NOW'))";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, target.getName());
            preparedStatement.setString(3, rewardType.name());
            preparedStatement.setInt(4, reward);

            preparedStatement.executeUpdate();
            CBounty.getInstance().getServer().getPluginManager().callEvent(new BountyCreateEvent(player, target, reward, rewardType));
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }
    }

    @Override
    public void createRandomBounty(@NotNull Player target, @NotNull RewardType rewardType, int reward) {
        String query = "INSERT INTO bounty (PLAYER, TARGET, REWARD_TYPE, REWARD, BOUNTY_DATE) VALUES (?, ?, ?, ?, DATETIME('NOW'))";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, ConfigKeys.RANDOM_BOUNTY_PLAYER_VALUE.getString());
            preparedStatement.setString(2, target.getName());
            preparedStatement.setString(3, rewardType.name());
            preparedStatement.setInt(4, reward);

            preparedStatement.executeUpdate();
            CBounty.getInstance().getServer().getPluginManager().callEvent(new BountyCreateEvent(null, target, reward, rewardType));
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }
    }

    @Override
    public boolean isSenderIsRandom(@NotNull Player player) {
        String query = "SELECT PLAYER FROM bounty WHERE TARGET = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String sender = resultSet.getString("PLAYER");
                return sender.equals(ConfigKeys.RANDOM_BOUNTY_PLAYER_VALUE.getString());
            }
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }

        return false;
    }

    @Override
    public List<Bounty> getBounties() {
        List<Bounty> bounties = new ArrayList<>();

        String query = "SELECT * FROM bounty";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int reward = resultSet.getInt("REWARD");
                String target = resultSet.getString("TARGET");
                String player = resultSet.getString("PLAYER");
                String reward_type = resultSet.getString("REWARD_TYPE");
                bounties.add(new Bounty(id, player, target, RewardType.valueOf(reward_type), reward));
            }
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }

        return bounties;
    }

    @Override
    public int getStreak(@NotNull OfflinePlayer player) {
        String updateQuery = "UPDATE bounty SET STREAK = (julianday('now') - julianday((SELECT MAX(BOUNTY_DATE) FROM bounty WHERE TARGET = ?))) WHERE TARGET = ?";
        String selectQuery = "SELECT STREAK FROM bounty WHERE TARGET = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            updateStatement.setString(1, player.getName());
            updateStatement.setString(2, player.getName());
            updateStatement.executeUpdate();

            selectStatement.setString(1, player.getName());
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) return resultSet.getInt("STREAK");
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public boolean isBounty(@NotNull Player player) {
        String query = "SELECT TARGET FROM bounty WHERE TARGET = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }

        return false;
    }

    @Override
    public void changeReward(@NotNull Player player, int newReward) {
        String query = "UPDATE bounty SET REWARD = ? WHERE TARGET = ?";

        try {
            try (PreparedStatement updateStatement = getConnection().prepareStatement(query)) {
                updateStatement.setInt(1, getReward(player) + newReward);
                updateStatement.setString(2, player.getName());
                updateStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }
    }

    @Override
    public int getReward(@NotNull Player player) {
        String query = "SELECT REWARD FROM bounty WHERE TARGET = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            int reward;

            if (resultSet.next()) {
                reward = resultSet.getInt("REWARD");
                return reward;
            }
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public RewardType getRewardType(@NotNull Player player) {
        String query = "SELECT REWARD_TYPE FROM bounty WHERE TARGET = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            RewardType rewardType;

            if (resultSet.next()) {
                rewardType = RewardType.valueOf(resultSet.getString("REWARD_TYPE"));
                return rewardType;
            }
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }

        return RewardType.MONEY;
    }

    @Override
    public Player getSender(@NotNull Player player) {
        String query = "SELECT PLAYER FROM bounty WHERE TARGET = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return Bukkit.getPlayerExact(resultSet.getString("PLAYER"));
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }

        return null;
    }

    @Override
    public void removeBounty(@NotNull Player player) {
        String query = "DELETE FROM bounty WHERE TARGET = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }
    }

    @Override
    public List<Top> getTop(int number) {
        List<Top> topStreaks = new ArrayList<>();
        String query = "SELECT TARGET, STREAK FROM bounty ORDER BY STREAK DESC LIMIT ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, number);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(resultSet.getString("TARGET"));
                    int streak = resultSet.getInt("STREAK");

                    topStreaks.add(new Top(player, streak));
                }
            }
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }

        return topStreaks;
    }

    @Override
    public String getTopStreakPlayer(int top) {
        String playerName = null;
        String query = "SELECT TARGET FROM bounty ORDER BY STREAK DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, top - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) playerName = resultSet.getString("TARGET");
            }
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }

        return playerName;
    }

    @Override
    public int getTopStreak(int top) {
        String query = "SELECT STREAK FROM bounty ORDER BY STREAK DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, top - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("STREAK");
            }
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public void reconnect() {
        try {
            if (getConnection() != null && !getConnection().isClosed()) getConnection().close();
            new SQLite();
        } catch (SQLException | ClassNotFoundException exception) {
            BountyLogger.error(exception.getMessage());
        }
    }

    @Override
    public boolean reachedMaximumBounty(@NotNull Player player) {
        String query = "SELECT COUNT(*) AS total FROM bounty WHERE PLAYER = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int totalItems = resultSet.getInt("total");
                    return totalItems >= ConfigKeys.MAXIMUM_BOUNTY.getInt();
                }
            }
        } catch (SQLException exception) {
            BountyLogger.error(exception.getMessage());
        }
        return false;
    }


    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException exception) {
                BountyLogger.error(exception.getMessage());
            }
        }
    }
}
