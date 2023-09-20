package de.banarnia.fancyhomes.manager;

import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.api.sql.Database;
import de.banarnia.fancyhomes.config.HomeConfig;
import de.banarnia.fancyhomes.data.Home;
import de.banarnia.fancyhomes.data.HomeData;
import de.banarnia.fancyhomes.data.SQLHomeData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HomeManager implements FancyHomesAPI {

    private FancyHomes plugin;
    private HomeConfig config;
    private Database database;
    
    private HashMap<UUID, HomeData> cachedPlayerData = new HashMap<>();

    public HomeManager(FancyHomes plugin, HomeConfig config) {
        this.plugin = plugin;
        this.config = config;

        init();
    }

    private void init() {
        this.database = config.getDatabase();
        this.database.openConnection();

        switch (config.getStorageMethod()) {
            case MySQL:
                database.executeUpdate("CREATE TABLE IF NOT EXISTS fancyhomes_homedata (" +
                        "ID int PRIMARY KEY AUTO_INCREMENT," +
                        "Name varchar(16) NOT NULL," +
                        "UUID varchar(36) NOT NULL," +
                        "Player varchar(16) DEFAULT '?'," +
                        "Created datetime DEFAULT NOW()," +
                        "World varchar(16) NOT NULL," +
                        "X double NOT NULL," +
                        "Y double NOT NULL," +
                        "Z double NOT NULL," +
                        "Yaw float NOT NULL," +
                        "Pitch float NOT NULL" +
                        ");");
                break;
            case SQLite:
                database.executeUpdate("CREATE TABLE IF NOT EXISTS fancyhomes_homedata (" +
                        "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "Name TEXT NOT NULL," +
                        "UUID TEXT NOT NULL," +
                        "Player TEXT DEFAULT '?'," +
                        "Created INTEGER," +
                        "World TEXT NOT NULL," +
                        "X REAL NOT NULL," +
                        "Y REAL NOT NULL," +
                        "Z REAL NOT NULL," +
                        "Yaw REAL NOT NULL," +
                        "Pitch REAL NOT NULL" +
                        ");");
                break;
        }
    }

    public CompletableFuture<HomeData> loadPlayer(UUID playerId) {
        if (isLoaded(playerId))
            return CompletableFuture.completedFuture(cachedPlayerData.get(playerId));

        return CompletableFuture.supplyAsync(() -> {
            HomeData homeData = null;
            String playerName = null;
            if (Bukkit.getOfflinePlayer(playerId) != null)
                playerName = Bukkit.getOfflinePlayer(playerId).getName();

            switch (config.getStorageMethod()) {
                case SQLite, MySQL -> homeData = new SQLHomeData(playerId, playerName, database, config.getMaxHomes());
            }
            cachedPlayerData.put(playerId, homeData);

            homeData.load();

            return homeData;
        });
    }

    public HomeData getHomeData(Player player) {
        if (player == null)
            return null;

        return getHomeData(player.getUniqueId());
    }

    public HomeData getHomeData(UUID playerId) {
        return loadPlayer(playerId).join();
    }

    public boolean isLoaded(UUID playerId) {
        return cachedPlayerData.containsKey(playerId);
    }

    public void removeFromCache(UUID playerId) {
        this.cachedPlayerData.remove(playerId);
    }

    @Override
    public HashMap<String, Home> getHomes(Player player) {
        return getHomeData(player).getPlayersHomes();
    }

    @Override
    public boolean addHome(Player player, String name, Location location) {
        if (player == null || location == null)
            return false;

        name = name.replace(" ", "_");

        return getHomeData(player).addHome(name, location);
    }

    @Override
    public boolean deleteHome(Player player, String name) {
        if (player == null)
            return false;

        return getHomeData(player).deleteHome(name);
    }

    @Override
    public int getHomeLimit(Player player) {
        if (player == null)
            return 0;

        // TODO
        return 0;
    }
}
