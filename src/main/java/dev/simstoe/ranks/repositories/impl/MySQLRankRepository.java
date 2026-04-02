package dev.simstoe.ranks.repositories.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.simstoe.ranks.models.RankPlayer;
import dev.simstoe.ranks.models.enums.Rank;
import dev.simstoe.ranks.repositories.RankRepository;

import java.sql.SQLException;
import java.util.UUID;

public final class MySQLRankRepository implements RankRepository {
    private final HikariDataSource dataSource;

    public MySQLRankRepository(String host, int port, String database, String username, String password) {
        var config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(10);

        this.dataSource = new HikariDataSource(config);
        this.setupTable();
    }

    @Override
    public void savePlayer(RankPlayer player) {
        try (var connection = this.dataSource.getConnection();
             var prepareStatement = connection.prepareStatement("INSERT INTO player_ranks (uuid, `rank`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `rank` = ?")) {
            prepareStatement.setString(1, player.uuid().toString());
            prepareStatement.setString(2, player.rank().name());
            prepareStatement.setString(3, player.rank().name());
            prepareStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public RankPlayer loadPlayer(UUID uuid) {
        try (var connection = this.dataSource.getConnection();
             var prepareStatement = connection.prepareStatement("SELECT `rank` FROM player_ranks WHERE uuid = ?")) {

            prepareStatement.setString(1, uuid.toString());

            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new RankPlayer(uuid, Rank.valueOf(resultSet.getString("rank")));
                }
            }
        } catch (SQLException | IllegalArgumentException exception) {
            exception.printStackTrace();
            return null;
        }
        return null;
    }

    public void close() {
        if (dataSource != null) dataSource.close();
    }

    private void setupTable() {
        try (var connection = this.dataSource.getConnection(); var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS player_ranks (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "`rank` VARCHAR(32) NOT NULL)");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
