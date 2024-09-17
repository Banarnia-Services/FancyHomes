package de.banarnia.fancyhomes;

import de.banarnia.fancyhomes.api.UtilThread;
import de.banarnia.fancyhomes.data.HomeData;
import de.banarnia.fancyhomes.data.storage.Home;
import de.banarnia.fancyhomes.events.DelhomeEvent;
import de.banarnia.fancyhomes.events.SethomeEvent;
import de.banarnia.fancyhomes.lang.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface FancyHomesAPI {

    static FancyHomesAPI get() {
        return FancyHomes.getInstance().getManager();
    }

    /**
     * Get the HomeData of a player.
     * @param playerId Player UUID.
     * @return HomeData of the player.
     */
    CompletableFuture<HomeData> getHomeData(UUID playerId);

    /**
     * Tries to add a new home for the player.
     * @param playerId Player UUID.
     * @param name Home name.
     * @param location Home location.
     * @return True if home was created, else false.
     */
    default CompletableFuture<Boolean> addHome(Player issuer, UUID playerId, String name, Location location) {
        if (name == null)
            name = "Default";
        if (playerId == null || location == null)
            throw new IllegalArgumentException("You need to specify a player id, home name and location.");

        name = name.replace(" ", "_");
        String finalName = name.substring(0, Math.min(name.length(), 20));
        return getHomeData(playerId)
                .thenApplyAsync(data -> {
                    try {
                        boolean success = UtilThread.runSync(FancyHomes.getInstance(), () -> {
                            int homeLimit = getHomeLimit(playerId);
                            int homeAmount = data.getHomeMap().size();
                            boolean limitReached = homeAmount >= homeLimit && homeLimit >= 0;
                            boolean limitExceeded = homeAmount > homeLimit && homeLimit >= 0;

                            // Check if home name contains underscores.
                            if (finalName.contains("_")) {
                                issuer.sendMessage(Message.COMMAND_ERROR_SETHOME_UNDERSCORE.get());
                                return false;
                            }

                            // Check if player already has more homes than allowed.
                            if (limitExceeded) {
                                issuer.sendMessage(Message.COMMAND_ERROR_SETHOME_LIMIT_EXCEEDED.get());
                                return false;
                            }

                            // Check if player has the exact amount of homes he is allowed to.
                            if (limitReached && !data.hasHome(finalName)) {
                                issuer.sendMessage(Message.COMMAND_ERROR_SETHOME_LIMIT_REACHED.get());
                                return false;
                            }

                            SethomeEvent event = new SethomeEvent(issuer, data, finalName, location);
                            event.callEvent();
                            if (event.isCancelled()) {
                                issuer.sendMessage(Message.COMMAND_ERROR_SETHOME_CANCELED.replace("%home%", finalName));
                                return false;
                            }

                            return true;
                        }).get();

                        if (!success)
                            return false;

                    } catch (Exception e) {
                        return false;
                    }

                    if (data.hasHome(finalName)) {
                        return data.updateHome(finalName, location).thenApply(success -> {
                            if (success)
                                UtilThread.sendMessageSync(FancyHomes.getInstance(),
                                        issuer, Message.COMMAND_INFO_SETHOME_RELOCATED.replace("%home%", finalName));
                            else
                                UtilThread.sendMessageSync(FancyHomes.getInstance(),
                                        issuer, Message.COMMAND_ERROR_SETHOME_RELOCATION_FAILED.get());

                            return success;
                        }).join();
                    }

                    boolean success = data.addHome(finalName, location).join();
                    if (success)
                        UtilThread.sendMessageSync(FancyHomes.getInstance(), issuer,
                                Message.COMMAND_INFO_SETHOME_CREATED.replace("%home%", finalName));
                    else
                        UtilThread.sendMessageSync(FancyHomes.getInstance(), issuer,
                                Message.COMMAND_ERROR_SETHOME_FAILED.get());

                    return success;
                });
    }

    /**
     * Tries to delete a users home.
     * @param issuer Command issuer.
     * @param playerId Player UUID.
     * @param name Home name.
     * @return True if operation was successful, else false.
     */
    default CompletableFuture<Boolean> deleteHome(Player issuer, UUID playerId, String name) {
        if (issuer == null || playerId == null || name == null)
            throw new IllegalArgumentException("You need to specify a player id and home name.");

        return getHomeData(playerId)
                .thenApply(data -> {
                    try {
                        boolean success = UtilThread.runSync(FancyHomes.getInstance(), () -> {
                            if (!data.hasHome(name)) {
                                issuer.sendMessage(Message.COMMAND_ERROR_HOME_NOT_FOUND.replace("%home%", name));
                                return false;
                            }

                            Home home = data.getHome(name);
                            DelhomeEvent event = new DelhomeEvent(issuer, data, home);
                            event.callEvent();
                            if (event.isCancelled()) {
                                issuer.sendMessage(Message.COMMAND_ERROR_DELHOME_CANCELED.replace("%home%", name));
                                return false;
                            }

                            return true;
                        }).get();

                        return success ? data : null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .thenApplyAsync(data -> {
                    boolean success = data.deleteHome(name).join();
                    if (success)
                        UtilThread.sendMessageSync(FancyHomes.getInstance(), issuer, Message.COMMAND_INFO_DELHOME_SUCCESS.replace("%home%", name));
                    else
                        UtilThread.sendMessageSync(FancyHomes.getInstance(), issuer, Message.COMMAND_ERROR_DELHOME_FAILED.get());

                    return success;
                });
    }

    /**
     * Get the max amount of homes the player can have.
     * @param playerId Player UUID.
     * @return Max amount of homes. Negative values correspond to infinite homes.
     */
    int getHomeLimit(UUID playerId);

    /**
     * Check if player has cooldown before next home command.
     * @param player Player instance.
     * @return True if player ahs cooldown, else false.
     */
    boolean hasCooldown(Player player);

    /**
     * Check if the player is in the warmup phase.
     * @param player Player instance.
     * @return True if player is in warmup phase, else false.
     */
    boolean isInWarmup(Player player);

    /**
     * Teleport a player to a certain home.
     * @param player Player instance.
     * @param home Home instance.
     */
    void teleport(Player player, Home home);

    /**
     * Check if the player has reached to max amount of homes.
     * @param playerId Players UUID.
     * @return True if home limit is reached, else false.
     */
    default CompletableFuture<Boolean> homeLimitReached(UUID playerId) {
        return getHomeNames(playerId).thenApply(names -> {
            int limit = getHomeLimit(playerId);
            if (limit < 0)
                return false;

            return names.size() >= getHomeLimit(playerId);
        });
    }

    /**
     * Check if the player has exceeded his home limit.
     * @param playerId Players UUID.
     * @return True if home limit is exceeded, else false.
     */
    default CompletableFuture<Boolean> homeLimitExceeded(UUID playerId) {
        return getHomeNames(playerId).thenApply(names -> {
            int limit = getHomeLimit(playerId);
            if (limit < 0)
                return false;

            return names.size() > getHomeLimit(playerId);
        });
    }

    /**
     * Get a map of all players homes.
     * @param playerId Player UUID.
     * @return Map of players homes and their names.
     */
    default CompletableFuture<Map<String, Home>> getHomeMap(UUID playerId) {
        return getHomeData(playerId).thenApply(data -> {
            return data.getHomeMap();
        });
    }

    /**
     * Get a collection of all home names of the player.
     * @param playerId Player UUID.
     * @return Collection of home names.
     */
    default CompletableFuture<Set<String>> getHomeNames(UUID playerId) {
        return getHomeMap(playerId).thenApply(Map::keySet);
    }

    default CompletableFuture<Integer> getHomeAmount(UUID playerId) {
        return getHomeNames(playerId).thenApply(Set::size);
    }

    /**
     * Get a home instance from a player by its name.
     * @param playerId Players UUID.
     * @param homeName Home name.
     * @return Home instance if found, else null.
     */
    default CompletableFuture<Home> getHome(UUID playerId, String homeName) {
        return getHomeMap(playerId).thenApply(map -> map.get(homeName));
    }

    /**
     * Send a message to the player that lists all targets homes.
     * @param issuer Issuer.
     * @param target Target.
     */
    default void listHomes(Player issuer, UUID target) {
        if (issuer == null || target == null)
            throw new IllegalArgumentException("You need to specify an issuer and a target.");

        getHomeMap(target).thenAccept(map -> {
            UtilThread.runSync(FancyHomes.getInstance(), () -> {
                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
                String targetName = targetPlayer != null ? targetPlayer.getName() : target.toString();

                int homeLimit = getHomeLimit(target);
                issuer.sendMessage("§6" + targetName + "'s Homes §8[§e" + map.size() +
                        (homeLimit >= 0 ? "§7/§a" + getHomeLimit(target) : "") +
                        "§8]");
                map.values().forEach(home -> issuer.sendMessage("§7- §8[§7" + home.getWorldName() + "§8] §e" + home.getName()));

                return true;
            });
        });
    }

}
