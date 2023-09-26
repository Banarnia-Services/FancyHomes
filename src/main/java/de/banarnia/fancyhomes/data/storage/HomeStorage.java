package de.banarnia.fancyhomes.data.storage;

import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.data.HomeData;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class HomeStorage implements HomeData {

    protected FancyHomes plugin;

    protected UUID playerId;

    protected HashMap<String, Home> homes = new HashMap<>();

    public HomeStorage(FancyHomes plugin, UUID playerId) {
        this.plugin = plugin;
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return Bukkit.getOfflinePlayer(playerId).getName();
    }

    @Override
    public UUID getUuid() {
        return playerId;
    }

    @Override
    public CompletableFuture<Boolean> addHome(Home home) {
        if (home == null || hasHome(home.getName()))
            return CompletableFuture.completedFuture(false);

        return saveHomeInStorage(home).thenApply(saved -> {
            if (saved)
                homes.put(home.getName(), home);
            return saved;
        });
    }

    /**
     * Tries to add a new home.
     * @param name Home name.
     * @param location Home location.
     * @return Completable future that indicates if the operation was successful.
     */
    public CompletableFuture<Boolean> addHome(String name, Location location) {
        if (hasHome(name) || location == null || location.getWorld() == null)
            return CompletableFuture.completedFuture(false);

        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        Home home = new Home(name, System.currentTimeMillis(), null, worldName, x, y, z, yaw, pitch);
        return saveHomeInStorage(home).thenApply(saved -> {
            if (saved)
                homes.put(name, home);
            return saved;
        });
    }

    public CompletableFuture<Boolean> updateHome(String homeName, Location newLocation) {
        if (!hasHome(homeName) || newLocation == null || newLocation.getWorld() == null)
            return CompletableFuture.completedFuture(false);

        long timestamp = System.currentTimeMillis();
        return updateHomeLocationInStorage(homeName, newLocation, timestamp).thenApply(success -> {
            Home home = getHome(homeName);
            if (success)
                home.updateLocation(newLocation, timestamp);

            return success;
        });
    }

    public CompletableFuture<Boolean> updateHome(String homeName, String newIcon) {
        if (!hasHome(homeName))
            return CompletableFuture.completedFuture(false);

        return updateHomeIconInStorage(homeName, newIcon).thenApply(success -> {
            Home home = getHome(homeName);
            if (success)
                home.setIcon(newIcon);

            return success;
        });
    }

    /**
     * Tries to delete a home.
     * @param name Home name.
     * @return Completable future that indicates if the operation was successful.
     */
    public CompletableFuture<Boolean> deleteHome(String name) {
        if (!hasHome(name))
            return CompletableFuture.completedFuture(false);

        return deleteHomeFromStorage(name).thenApply(deleted -> {
            if (deleted)
                homes.remove(name);
            return deleted;
        });
    }

    @Override
    public Map<String, Home> getHomeMap() {
        return homes;
    }

    /**
     * Init the storage.
     */
    public abstract CompletableFuture<Boolean> init();

    /**
     * Reloads the homes from the storage.
     * @return True if reloading was successful, else false.
     */
    public abstract CompletableFuture<Boolean> loadHomesFromStorage();

    /**
     * Save a home to the storage.
     * @param home Home instance.
     * @return True if successfully stored, else false.
     */
    protected abstract CompletableFuture<Boolean> saveHomeInStorage(Home home);

    /**
     * Delete a home from the storage.
     * @param homeName Home name.
     * @return True if successful, else false.
     */
    protected abstract CompletableFuture<Boolean> deleteHomeFromStorage(String homeName);

    /**
     * Update the location of a home in the storage.
     * @param homeName Home name.
     * @param location New home location.
     * @return True if update was successful, else false.
     */
    protected abstract CompletableFuture<Boolean> updateHomeLocationInStorage(String homeName, Location location, long timestamp);

    /**
     * Update the location of a home in the storage.
     * @param homeName Home name.
     * @param newIcon New home icon.
     * @return True if update was successful, else false.
     */
    protected abstract CompletableFuture<Boolean> updateHomeIconInStorage(String homeName, String newIcon);


}
