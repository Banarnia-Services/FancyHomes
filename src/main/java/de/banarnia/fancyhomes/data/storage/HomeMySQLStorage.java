package de.banarnia.fancyhomes.data.storage;

import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.api.sql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HomeMySQLStorage extends HomeStorage {

    private MySQL database;

    public HomeMySQLStorage(FancyHomes plugin, UUID playerId, MySQL database) {
        super(plugin, playerId);
        this.database = database;
    }

    @Override
    public CompletableFuture<Boolean> init() {
        return database.executeUpdateAsync("CREATE TABLE IF NOT EXISTS fancyhomes_data (" +
            "ID int NOT NULL AUTO_INCREMENT," +
            "UUID varchar(36) NOT NULL," +
            "Player varchar(16)," +
            "Name varchar(20) NOT NULL," +
            "Created timestamp NOT NULL DEFAULT NOW()," +
            "Icon varchar(20)," +
            "World varchar(36) NOT NULL," +
            "X double NOT NULL," +
            "Y double NOT NULL," +
            "Z double NOT NULL," +
            "Yaw float NOT NULL," +
            "Pitch float NOT NULL," +
            "PRIMARY KEY (ID)" +
            ");")
            .thenApplyAsync(success -> {
                if (success) {
                    ResultSet rs = null;
                    try {
                        DatabaseMetaData md = database.getConnection().getMetaData();
                        rs = md.getColumns(null, null, "fancyhomes_data", "Icon");
                        if (!rs.next())
                            database.executeUpdate("ALTER TABLE fancyhomes_data ADD COLUMN Icon varchar(20) AFTER Created;");
                    } catch (SQLException e) {
                    } finally {
                        try {
                            if (rs != null)
                                rs.close();
                        } catch (SQLException e) {
                        }
                    }
                }

                return success;
            })
            .thenApply(success -> Bukkit.getOfflinePlayer(playerId))
            .thenApplyAsync(op -> {
                if (op == null)
                    return true;

                return database.executeUpdate("UPDATE fancyhomes_data SET Player=? WHERE UUID=?;", op.getName(), playerId.toString());
            });
    }

    @Override
    public CompletableFuture<Boolean> loadHomesFromStorage() {
        return database.executeQueryAsync("SELECT * FROM fancyhomes_data WHERE UUID=?;", playerId.toString())
                .thenApply(resultSet -> {
                    try {
                        while (resultSet.next()) {
                            String name = resultSet.getString("Name");
                            Timestamp created = resultSet.getTimestamp("Created");
                            String icon = resultSet.getString("Icon");
                            String worldName = resultSet.getString("World");
                            double x = resultSet.getDouble("X");
                            double y = resultSet.getDouble("Y");
                            double z = resultSet.getDouble("Z");
                            float yaw = resultSet.getFloat("Yaw");
                            float pitch = resultSet.getFloat("Pitch");

                            Home home = new Home(name, created.getTime(), icon, worldName, x, y, z, yaw, pitch);
                            homes.put(name, home);
                        }

                        return true;
                    } catch (SQLException e) {
                        return false;
                    }
                });
    }

    @Override
    protected CompletableFuture<Boolean> saveHomeInStorage(Home home) {
        String name = home.getName();
        Timestamp created = home.getSqlTimestamp();
        String icon = home.getIcon();
        String worldName = home.getWorldName();
        double x = home.getX();
        double y = home.getY();
        double z = home.getZ();
        float yaw = home.getYaw();
        float pitch = home.getPitch();

        return database.executeUpdateAsync("INSERT INTO fancyhomes_data VALUES (" +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                    ");", null, playerId.toString(), getPlayerName(), name, created, icon, worldName, x, y, z, yaw, pitch);
    }

    @Override
    protected CompletableFuture<Boolean> deleteHomeFromStorage(String homeName) {
        return database.executeUpdateAsync("DELETE FROM fancyhomes_data WHERE UUID=? AND Name=?;",
                    playerId.toString(), homeName);
    }

    @Override
    protected CompletableFuture<Boolean> updateHomeLocationInStorage(String homeName, Location location, long timestamp) {
        String worldName = location.getWorld().getName();
        Timestamp created = new Timestamp(timestamp);
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        return database.executeUpdateAsync("UPDATE fancyhomes_data SET " +
                            "Created=?,World=?,X=?,Y=?,Z=?,Yaw=?,Pitch=? " +
                            "WHERE UUID=? AND Name=?;",
                created, worldName, x, y, z, yaw, pitch, playerId.toString(), homeName);
    }

    @Override
    protected CompletableFuture<Boolean> updateHomeIconInStorage(String homeName, String newIcon) {
        return database.executeUpdateAsync("UPDATE fancyhomes_data SET " +
                        "Icon=? " +
                        "WHERE UUID=? AND Name=?;",
                newIcon, playerId.toString(), homeName);
    }
}
