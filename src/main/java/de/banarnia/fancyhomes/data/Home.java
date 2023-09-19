package de.banarnia.fancyhomes.data;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class Home {

    @Getter
    private int id;

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

    public Home(int id, UUID playerId, String playerName, Timestamp created,
                String worldName, double x, double y, double z, float yaw, float pitch) {
        this.id = id;
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

}
