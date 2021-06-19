package me.youhavetrouble.happyhours;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.time.Instant;

public class HappyHour {

    private final String name;
    private final NamespacedKey namespacedKey;
    private long expireTime;

    public HappyHour(String name, Plugin plugin, long eventTime) {
        this.namespacedKey = new NamespacedKey(plugin, name);
        this.name = name;
        Instant instant = Instant.now();
        this.expireTime = instant.getEpochSecond() + eventTime;
    }

    public String getName() {
        return name;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public long getSecondsLeft() {
        long s = expireTime - Instant.now().getEpochSecond();
        if (s < 0)
            return 0;
        return s;
    }

    public void AddActiveTime(long time) {
        if (time <= 0) return;
        if (isActive()) {
            expireTime += time;
        } else {
            expireTime = Instant.now().getEpochSecond() + time;
        }
    }

    public boolean isActive() {
        return Instant.now().getEpochSecond() <= expireTime;
    }

    protected void saveToStorage() {
        HappyHours.getStorage().updateEntry(namespacedKey, expireTime);
    }
}
