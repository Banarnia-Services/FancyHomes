package de.banarnia.fancyhomes.data;

import de.banarnia.fancyhomes.api.sql.Database;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public class SQLHomeData implements HomeData {

    private UUID playerId;
    private String playerName;
    private Database database;
    private int maxHomes;
    private HashMap<String, Home> playerHomes = new HashMap<>();

    public SQLHomeData(UUID playerId, String playerName, Database database, int maxHomes) {
        // TODO: Remove static max homes.
        this.playerId = playerId;
        this.playerName = playerName != null ? playerName : "?";
        this.database = database;
        this.maxHomes = maxHomes;
    }

    public SQLHomeData(UUID playerId, Database database, int maxHomes) {
        this(playerId, null, database, maxHomes);
    }

    @Override
    public HashMap<String, Home> getPlayersHomes() {
        return playerHomes;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void load() {
        this.playerHomes.clear();

        if (playerName.length() >= 3)
            database.executeUpdate("UPDATE fancyhomes_homedata SET Player=? WHERE UUID=?;", playerName, playerId.toString());

        ResultSet rs = database.executeQuery("SELECT * FROM fancyhomes_homedata WHERE UUID=?;", playerId.toString());
        try {
            while (rs.next()) {
                String homeName = rs.getString("Name");
                UUID playerId = UUID.fromString(rs.getString("UUID"));
                String playerName = rs.getString("Player");
                Timestamp created = rs.getTimestamp("Created");
                String worldName = rs.getString("World");
                double x = rs.getDouble("X");
                double y = rs.getDouble("Y");
                double z = rs.getDouble("Z");
                float yaw = rs.getFloat("Yaw");
                float pitch = rs.getFloat("Pitch");

                Home home = new Home(homeName, playerId, playerName, created, worldName, x, y, z, yaw, pitch);
                playerHomes.put(homeName, home);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addHome(String name, Location location) {
        if (homeExists(name))
            return false;

        if (location == null || location.getWorld() == null)
            return false;

        if (!database.isConnected())
            return false;

        if (homeLimitReached())
            return false;

        Timestamp creation = Timestamp.valueOf(LocalDateTime.now());
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        database.executeUpdateAsync("INSERT INTO fancyhomes_homedata VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                null, name, playerId.toString(), playerName, creation, worldName, x, y, z, yaw, pitch);

        Home home = new Home(name, playerId, playerName, creation, worldName, x, y, z, yaw, pitch);
        playerHomes.put(name, home);
        return true;
    }

    @Override
    public boolean deleteHome(String name) {
        if (!homeExists(name))
            return false;

        if (!database.isConnected())
            return false;

        database.executeUpdateAsync("DELETE FROM fancyhomes_homedata WHERE UUID=? AND Name=?;",
                playerId.toString(), name);
        playerHomes.remove(name);

        return true;
    }

    @Override
    public boolean homeExists(String name) {
        return playerHomes.containsKey(name);
    }

    @Override
    public int getHomeLimit() {
        //TODO
        return maxHomes;
    }
}
