package me.youhavetrouble.happyhours.storage;

import org.bukkit.NamespacedKey;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    void createTables() throws SQLException;
    CompletableFuture<Void> updateEntry(NamespacedKey key, long timestamp);
    CompletableFuture<Long> getEntry(NamespacedKey key);

}
