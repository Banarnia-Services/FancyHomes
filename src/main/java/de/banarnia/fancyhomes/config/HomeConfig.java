package de.banarnia.fancyhomes.config;

import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.data.StorageMethod;
import de.banarnia.fancyhomes.api.config.Config;
import de.banarnia.fancyhomes.api.sql.Database;
import de.banarnia.fancyhomes.api.sql.MySQL;
import de.banarnia.fancyhomes.api.sql.SQLite;

import java.io.File;

public class HomeConfig {

    private FancyHomes plugin;

    private Config config;
    private Database database;

    public HomeConfig(FancyHomes plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    public String getLanguage() {
        return config.getString("language", "en");
    }

    public double getWarmupTime() {
        return config.getDouble("warmup-time", 0);
    }

    public void setWarmupTime(double warmupTime) {
        config.set("warmup-time", warmupTime);
        config.save();
    }

    public double getCooldownTime() {
        return config.getDouble("cooldown-time", 0);
    }

    public void setCooldownTime(double cooldownTime) {
        config.set("cooldown-time", cooldownTime);
        config.save();
    }

    public int getMaxHomes() {
        return config.getInt("max-homes", 1);
    }

    public void setMaxHomes(int maxHomes) {
        config.set("max-homes", maxHomes);
        config.save();
    }

    public StorageMethod getStorageMethod() {
        String method = config.getString("storage-method");
        if (method.equalsIgnoreCase("SQLite"))
            return StorageMethod.SQLite;
        else if (method.equalsIgnoreCase("MySQL"))
            return StorageMethod.MySQL;
        else {
            plugin.getLogger().warning("Invalid storage method configured in config.yml");
            plugin.getLogger().warning("Falling back to SQLite...");
            return StorageMethod.SQLite;
        }
    }

    /**
     * Get the database if configured.
     * @return Database instance.
     */
    public Database getDatabase() {
        if (database != null)
            return database;

        StorageMethod storageMethod = getStorageMethod();
        switch (storageMethod) {
            case SQLite -> this.database = new SQLite(plugin.getLogger(), new File(plugin.getDataFolder(), "homes.db"));
            case MySQL -> {
                String host = config.getString("mysql.host", "127.0.0.1");
                int port = config.getInt("mysql.port", 3306);
                String database = config.getString("mysql.database", "database");
                String user = config.getString("mysql.user", "user");
                String password = config.getString("mysql.password", "password");

                this.database = new MySQL(database, plugin.getLogger(), host, port, user, password);
            }
        }

        return this.database;
    }

}
