package de.banarnia.fancyhomes;

import de.banarnia.fancyhomes.data.Home;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public interface FancyHomesAPI {

    /**
     * Get api instance.
     * @return Api instance.
     */
    static FancyHomesAPI get() {
        return FancyHomes.getInstance().getManager();
    }

    /**
     * Get a map of all players homes and their names.
     * @param player Player to look up.
     * @return Map of all players homes.
     */
    HashMap<String, Home> getHomes(Player player);

    /**
     * Get a certain home of the player with the given home name.
     * @param player Player to look up.
     * @param name Home name.
     * @return Home if exists, else null.
     */
    default Home getHome(Player player, String name) {
        if (!hasHome(player, name))
            return null;

        return getHomes(player).get(name);
    }

    /**
     * Attempts to add a new home for the player.
     * @param player Corresponding players.
     * @param name Name of the home.
     * @param location Home location.
     * @return True if home was created, else false.
     */
    boolean addHome(Player player, String name, Location location);

    /**
     * Attempts to delete a players home if it exists.
     * @param player Corrsponding player.
     * @param name Home name.
     * @return True if home was deleted, else false.
     */
    boolean deleteHome(Player player, String name);

    /**
     * Check if a player has a home with the given name.
     * @param player Player to loop up.
     * @param name Home name.
     * @return True if home was found, else false.
     */
    default boolean hasHome(Player player, String name) {
        return getHomes(player).containsKey(name);
    }

    /**
     * Get a players home limit.
     * @param player Player instance.
     * @return Players home limit.
     */
    int getHomeLimit(Player player);

}
