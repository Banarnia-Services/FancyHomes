package de.banarnia.fancyhomes.config;

import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.api.config.Config;
import de.banarnia.api.sql.Database;
import de.banarnia.api.sql.MySQL;
import de.banarnia.fancyhomes.data.storage.StorageMethod;

public class HomeConfig {

    private final FancyHomes plugin;

    private final Config config;

    private Database database;

    public HomeConfig(FancyHomes plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void reload() {
        config.loadConfig();
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
        String method = config.getString("storage-method", StorageMethod.File.toString());
        if (method.equalsIgnoreCase("MySQL"))
            return StorageMethod.MySQL;

        return StorageMethod.File;
    }

    public boolean debugMode() {
        return config.getBoolean("debug");
    }

    public Database getDatabase() {
        if (database != null)
            return database;

        if (getStorageMethod() == StorageMethod.MySQL) {
            String host = config.getString("mysql.host");
            int port = config.getInt("mysql.port");
            String database = config.getString("mysql.database");
            String user = config.getString("mysql.user");
            String password = config.getString("mysql.password");

            this.database = new MySQL(database, plugin.getLogger(), host, port, user, password);
        }

        return database;
    }

}
