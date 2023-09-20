package de.banarnia.fancyhomes.data;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.sql.Timestamp;
import java.util.UUID;

public class Home {

    @Getter
    private String name;

    @Getter
    private UUID playerId;
    @Getter
    private String playerName;
    @Getter
    private Timestamp created;

    @Getter
    private String worldName;
    @Getter
    private double x,y,z;
    @Getter
    private float yaw, pitch;

    public Home(String name, UUID playerId, String playerName, Timestamp created,
                String worldName, double x, double y, double z, float yaw, float pitch) {
        this.name = name;
        this.playerId = playerId;
        this.playerName = playerName;
        this.created = created;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Check if the home is usable.
     * @return True if the home location is loaded, else false.
     */
    public boolean isLoaded() {
        return getLocation() != null;
    }

    /**
     * Get the world instance the home is located in.
     * @return Home world.
     */
    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    /**
     * Get location of the home if the world is loaded.
     * @return Location if the world is loaded, else null.
     */
    public Location getLocation() {
        World world = getWorld();
        if (world == null)
            return null;

        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    /**
     * Teleports the player to the home location, if it is loaded.
     * @param player Player to teleport.
     * @param cause Teleport cause.
     * @return True if player was teleported, else false.
     */
    public boolean teleport(Player player, PlayerTeleportEvent.TeleportCause cause) {
        if (!isLoaded())
            return false;

        return cause != null ? player.teleport(getLocation(), cause) : player.teleport(getLocation());
    }

    /**
     * Teleports the player to the home location, if it is loaded.
     * @param player Player to teleport.
     * @return True if player was teleported, else false.
     */
    public boolean teleport(Player player) {
        return teleport(player, null);
    }

}
