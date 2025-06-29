package de.banarnia.fancyhomes.data.storage;

import de.banarnia.api.UtilString;
import de.banarnia.fancyhomes.lang.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Home implements ConfigurationSerializable {

    private final String name;
    private long created;
    private String icon;

    private String worldName;
    private double x,y,z;
    private float yaw, pitch;

    public Home(String name, long created, String icon,
                String worldName, double x, double y, double z, float yaw, float pitch) {
        this.name = name;
        this.created = created;
        this.icon = icon;
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

    protected boolean updateLocation(Location newLocation, long timestamp) {
        if (newLocation == null || newLocation.getWorld() == null)
            return false;

        this.worldName = newLocation.getWorld().getName();
        this.x = newLocation.getX();
        this.y = newLocation.getY();
        this.z = newLocation.getZ();
        this.yaw = newLocation.getYaw();
        this.pitch = newLocation.getPitch();

        this.created = timestamp;

        return true;
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

        player.sendMessage(Message.COMMAND_INFO_HOME_TELEPORT.replace("%home%", name));
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

    public Timestamp getSqlTimestamp() {
        return new Timestamp(created);
    }

    public World.Environment getWorldEnvironment() {
        World world = getWorld();
        if (world != null && world.getEnvironment() != null)
            return world.getEnvironment();

        if (UtilString.containsIgnoreCase(worldName, "nether"))
            return World.Environment.NETHER;
        if (UtilString.containsIgnoreCase(worldName, "end"))
            return World.Environment.THE_END;

        return World.Environment.NORMAL;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("Name", name);
        result.put("Created", created);
        if (icon != null)
            result.put("Icon", icon);
        result.put("World", worldName);
        result.put("X", x);
        result.put("Y", y);
        result.put("Z", z);
        result.put("Yaw", yaw);
        result.put("Pitch", pitch);

        return result;
    }

    public static Home deserialize(Map<String, Object> map) {
        String name = (String) map.get("Name");
        long created = (long) map.get("Created");
        String icon = null;
        if (map.containsKey("Icon"))
            icon = (String) map.get("Icon");
        String worldName = (String) map.get("World");
        double x = (double) map.get("X");
        double y = (double) map.get("Y");
        double z = (double) map.get("Z");
        float yaw = (float) ((double) map.get("Yaw"));
        float pitch = (float) ((double) map.get("Pitch"));

        return new Home(name, created, icon, worldName, x, y, z, yaw, pitch);
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public long getCreated() {
        return created;
    }
}
