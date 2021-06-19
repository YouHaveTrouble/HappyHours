package me.youhavetrouble.happyhours;

import me.youhavetrouble.happyhours.storage.Storage;
import me.youhavetrouble.happyhours.storage.StorageMysql;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;

public final class HappyHours extends JavaPlugin {

    private static final HashMap<NamespacedKey, HappyHour> events = new HashMap<>();
    private static Storage storage;
    private static HappyHours instance;

    @Override
    public void onEnable() {
        instance = this;
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

    public static NamespacedKey registerTimedEvent(String name, JavaPlugin plugin) {
        HappyHour happyHour = new HappyHour(name, plugin, 0);
        NamespacedKey key = happyHour.getNamespacedKey();
        long timestamp = storage.getEntry(key);
        long now = Instant.now().getEpochSecond();
        if (now < timestamp)
            happyHour.AddActiveTime(timestamp-now);
        events.putIfAbsent(key, happyHour);
        return key;
    }

    protected static Storage getStorage() {
        return storage;
    }

    public static HappyHours getInstance() {
        return instance;
    }


}
