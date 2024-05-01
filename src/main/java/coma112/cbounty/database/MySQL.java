package coma112.cbounty.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import coma112.cbounty.CBounty;
import coma112.cbounty.enums.keys.ConfigKeys;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.event.CreateBountyEvent;
import coma112.cbounty.managers.Bounty;
import coma112.cbounty.managers.Top;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter
public class MySQL extends AbstractDatabase {
    private final Connection connection;

    public MySQL(@NotNull ConfigurationSection section) throws ClassNotFoundException, SQLException {
        HikariConfig hikariConfig = new HikariConfig();

        String host = section.getString("host");
        String database = section.getString("database");
        String user = section.getString("username");
        String pass = section.getString("password");
        int port = section.getInt("port");
        boolean ssl = section.getBoolean("ssl");
        boolean certificateVerification = section.getBoolean("certificateverification");
        int poolSize = section.getInt("poolsize");
        int maxLifetime = section.getInt("lifetime");

        hikariConfig.setPoolName("BountyPool");
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime * 1000L);
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);
        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(ssl));
        if (!certificateVerification) hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        connection = dataSource.getConnection();
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS bounty (ID INT AUTO_INCREMENT PRIMARY KEY, PLAYER VARCHAR(255) NOT NULL, TARGET VARCHAR(255) NOT NULL, REWARD_TYPE VARCHAR(255) NOT NULL, REWARD INT, BOUNTY_DATE DATETIME, STREAK INT)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void createBounty(@NotNull Player player, @NotNull Player target, @NotNull RewardType rewardType, int reward) {
        String query = "INSERT INTO bounty (PLAYER, TARGET, REWARD_TYPE, REWARD, BOUNTY_DATE) VALUES (?, ?, ?, ?, NOW())";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, target.getName());
            preparedStatement.setString(3, rewardType.name());
            preparedStatement.setInt(4, reward);

            preparedStatement.executeUpdate();
            CBounty.getInstance().getServer().getPluginManager().callEvent(new CreateBountyEvent(player, target, reward, rewardType));
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
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
            throw new RuntimeException(exception);
        }

        return bounties;
    }

    @Override
    public int getStreak(@NotNull OfflinePlayer player) {
        String updateQuery = "UPDATE bounty SET STREAK = DATEDIFF(NOW(), (SELECT MAX(BOUNTY_DATE) FROM bounty WHERE TARGET = ?)) WHERE TARGET = ?";
        String selectQuery = "SELECT STREAK FROM bounty WHERE TARGET = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            updateStatement.setString(1, player.getName());
            updateStatement.setString(2, player.getName());
            updateStatement.executeUpdate();

            selectStatement.setString(1, player.getName());
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("STREAK");
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
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
            throw new RuntimeException(exception);
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
            throw new RuntimeException(exception);
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
            throw new RuntimeException(exception);
        }

        return RewardType.TOKEN;
    }

    @Override
    public Player getSender(@NotNull Player player) {
        String query = "SELECT PLAYER FROM bounty WHERE TARGET = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return Bukkit.getPlayerExact(resultSet.getString("PLAYER"));
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
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
            throw new RuntimeException(exception);
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
            throw new RuntimeException(exception);
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
            throw new RuntimeException(exception);
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
            throw new RuntimeException(exception);
        }

        return 0;
    }

    @Override
    public void reconnect(@NotNull ConfigurationSection section) {
        try {
            if (getConnection() != null && !getConnection().isClosed()) getConnection().close();
            new MySQL(Objects.requireNonNull(CBounty.getInstance().getConfiguration().getSection("database.mysql")));
        } catch (SQLException | ClassNotFoundException exception) {
            throw new RuntimeException("Failed to reconnect to the database", exception);
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
            throw new RuntimeException(exception);
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
                throw new RuntimeException(exception);
            }
        }
    }
}
