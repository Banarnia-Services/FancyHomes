package de.banarnia.fancyhomes.data;

import org.bukkit.Location;

import java.util.HashMap;

/**
 * Represents a players home data.
 */
public interface HomeData {

    /**
     * Get a map of all players homes and their names.
     * @return A list of homes that correspond to the player.
     */
    HashMap<String, Home> getPlayersHomes();

    /**
     * Get the last known player name.
     * @return Name of the player.
     */
    String getPlayerName();

    /**
     * Reloads the players data.
     */
    void load();

    /**
     * Adds a new home, if there is no home instance with the same name and the limit is not reached.
     * @param name Home name.
     * @param location Home location.
     * @return True, if the home was successfully added. Else false.
     */
    boolean addHome(String name, Location location);

    /**
     * Deletes the home with the given name.
     * @param name Home name.
     * @return True, if the home was deleted. False if the home does not exist.
     */
    boolean deleteHome(String name);

    /**
     * Checks if a home with the given name exists.
     * @param name Home name.
     * @return True, if the home exists. Else false.
     */
    boolean homeExists(String name);

    /**
     * Gets the amount of homes the player may have.
     * @return Max amount of homes.
     */
    int getHomeLimit();

    /**
     * Get the current amount of homes.
     * @return Amount of homes.
     */
    default int getHomeAmount() {
        return getPlayersHomes().size();
    }

    /**
     * Check if the player has reached the max amount of homes.
     * @return True if home limit was reached, else false.
     */
    default boolean homeLimitReached() {
        return getHomeAmount() >= getHomeLimit() && getHomeLimit() >= 0;
    }
}
