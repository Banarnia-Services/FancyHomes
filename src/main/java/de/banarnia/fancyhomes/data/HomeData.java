package de.banarnia.fancyhomes.data;

import de.banarnia.fancyhomes.data.storage.Home;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface HomeData {

    /**
     * Get players name.
     * @return Players name.
     */
    String getPlayerName();

    /**
     * Get players uuid.
     * @return Players uuid.
     */
    UUID getUuid();

    /**
     * Tries to add a new home.
     * @param name Home name.
     * @param location Home location.
     * @return True if home was created, else false.
     */
    CompletableFuture<Boolean> addHome(String name, Location location);

    /**
     * Update a homes location and creation timestamp.
     * @param homeName Home name.
     * @param newLocation New home location.
     * @return True if home was updated, else false.
     */
    CompletableFuture<Boolean> updateHome(String homeName, Location newLocation);

    /**
     * Tries to delete a home.
     * @param name Home name.
     * @return True if home was deleted, else false.
     */
    CompletableFuture<Boolean> deleteHome(String name);

    /**
     * Get a map of all players homes.
     * @return Map of all players homes and their names.
     */
    Map<String, Home> getHomeMap();

    /**
     * Check if a player has a home with the given name.
     * @param homeName Home name.
     * @return True if home exists, else false.
     */
    default boolean hasHome(String homeName) {
        return getHomeMap().containsKey(homeName);
    }

    /**
     * Get a players home by name.
     * @param homeName Home name.
     * @return Home if exists.
     */
    default Home getHome(String homeName) {
        return getHomeMap().get(homeName);
    }



}
