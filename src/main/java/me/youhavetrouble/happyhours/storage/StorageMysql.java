package me.youhavetrouble.happyhours.storage;

import org.bukkit.NamespacedKey;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class StorageMysql implements Storage {

    Connection connection;
    public static final String INSERT_ENTRY = "INSERT INTO `HappyHours` (id, endTimestamp) VALUES (?, ?) ON DUPLICATE KEY UPDATE endTimestamp = ?;";
    public static final String LOAD_TIME = "SELECT endTimestamp FROM `HappyHours` WHERE `id`=?;";

    public StorageMysql(String url) throws SQLException {
        connection = DriverManager.getConnection(url);
        DatabaseMetaData meta = connection.getMetaData();
        System.out.println("MySQL driver is " + meta.getDriverName());
        createTables();
    }

    @Override
    public void createTables() {
        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS `HappyHours` (`id` varchar(128) UNIQUE PRIMARY KEY, `endTimestamp` long);";
                statement.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> updateEntry(NamespacedKey key, long timestamp) {
        return CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement statement = connection.prepareStatement(INSERT_ENTRY);
                statement.setString(1, key.asString());
                statement.setLong(2, timestamp);
                statement.setLong(3, timestamp);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Long> getEntry(NamespacedKey key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement loaduser = connection.prepareStatement(LOAD_TIME);
                loaduser.setString(1, key.asString());
                ResultSet result = loaduser.executeQuery();
                if (result.next()) {
                    return result.getLong("endTimestamp");
                }
                return 0L;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return 0L;
            }
        });

    }
}
