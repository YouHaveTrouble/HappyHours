package me.youhavetrouble.happyhours.storage;

import org.bukkit.NamespacedKey;

import java.sql.SQLException;

public interface Storage {

    void createTables() throws SQLException;
    void updateEntry(NamespacedKey key, long timestamp);
    long getEntry(NamespacedKey key);

}
