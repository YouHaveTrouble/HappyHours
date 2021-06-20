package me.youhavetrouble.happyhours.storage;

import org.bukkit.NamespacedKey;

import java.sql.*;

public class StorageMysql implements Storage {

    Connection connection;
    public static final String INSERT_ENTRY = "INSERT IGNORE INTO `HappyHours` (id) VALUES (?);";
    public static final String UPDATE_TIME = "UPDATE `HappyHours` SET endTimestamp=? WHERE id=?;";
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
                String sql = "CREATE TABLE IF NOT EXISTS `HappyHours` (`id` varchar(128) UNIQUE PRIMARY KEY, `endTimestamp` long DEFAULT '0');";
                statement.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createEntry(NamespacedKey key) {
        try {
            PreparedStatement insertnew = connection.prepareStatement(INSERT_ENTRY);
            insertnew.setString(1, key.asString());
            insertnew.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() != 19) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateEntry(NamespacedKey key, long timestamp) {
        createEntry(key);
        try {
            PreparedStatement update = connection.prepareStatement(UPDATE_TIME);
            update.setLong(1, timestamp);
            update.setString(2, key.asString());
            update.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() != 19) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public long getEntry(NamespacedKey key) {
        try {
            createEntry(key);
            PreparedStatement loaduser = connection.prepareStatement(LOAD_TIME);
            loaduser.setString(1, key.asString());
            ResultSet result = loaduser.executeQuery();
            if (result.next()) {
                return result.getLong("endTimestamp");
            }
            return 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }
}
