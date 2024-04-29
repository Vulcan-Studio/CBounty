package coma112.cbounty.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import coma112.cbounty.CBounty;
import coma112.cbounty.enums.RewardType;
import coma112.cbounty.managers.Bounty;
import lombok.Getter;
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

        hikariConfig.setPoolName("AuctionPool");
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
        String query = "CREATE TABLE IF NOT EXISTS bounty (ID INT AUTO_INCREMENT PRIMARY KEY, PLAYER VARCHAR(255) NOT NULL, TARGET VARCHAR(255) NOT NULL, REWARD_TYPE VARCHAR(255) NOT NULL, REWARD INT)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void createBounty(@NotNull Player player, @NotNull Player target, @NotNull RewardType rewardType, int reward) {
        String query = "INSERT INTO bounty (PLAYER, TARGET, REWARD_TYPE, REWARD) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, target.getName());
            preparedStatement.setString(3, rewardType.name());
            preparedStatement.setInt(4, reward);

            preparedStatement.executeUpdate();
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
    public boolean isBounty(@NotNull Player player) {
        String query = "SELECT * FROM bounty WHERE TARGET = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());

            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isBounty;

            if (resultSet.next()) {
                isBounty = resultSet.getBoolean("TARGET");
                return isBounty;
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        return false;
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
