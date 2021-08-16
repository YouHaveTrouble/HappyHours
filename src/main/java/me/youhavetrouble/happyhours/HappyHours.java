package me.youhavetrouble.happyhours;

import me.youhavetrouble.happyhours.storage.Storage;
import me.youhavetrouble.happyhours.storage.StorageMysql;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public final class HappyHours extends JavaPlugin {

    private static final HashMap<NamespacedKey, HappyHour> events = new HashMap<>();
    private static Storage storage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        try {
            storage = new StorageMysql(getConfig().getString("mysql-url"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> events.values().forEach(HappyHour::saveToStorage), 100, 100);
    }

    public static HappyHour getHappyHour(NamespacedKey key) {
        return events.get(key);
    }

    /**
     * Register the event to cache it.
     * @param name Unique identifier for the event
     * @param plugin Plugin owning the event
     * @return CompletableFuture of NamespacedKey that will be used as key to access the event from getHappyHour()
     */
    public static CompletableFuture<NamespacedKey> registerTimedEvent(String name, JavaPlugin plugin) {
        HappyHour happyHour = new HappyHour(name, plugin, 0);
        NamespacedKey key = happyHour.getNamespacedKey();
        return storage.getEntry(key).thenApplyAsync(timestamp -> {
            long now = Instant.now().getEpochSecond();
            if (now < timestamp)
                happyHour.AddActiveTime(timestamp-now);
            events.putIfAbsent(key, happyHour);
            return key;
        });
    }

    protected static Storage getStorage() {
        return storage;
    }

}
