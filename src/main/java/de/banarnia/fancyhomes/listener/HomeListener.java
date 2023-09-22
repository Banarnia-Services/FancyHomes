package de.banarnia.fancyhomes.listener;


import de.banarnia.fancyhomes.manager.HomeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HomeListener implements Listener {

    private HomeManager manager;

    public HomeListener(HomeManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        // Load player data on join.
        manager.getHomeData(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        // Remove player from cache.
        manager.unloadPlayer(event.getPlayer().getUniqueId());

        // Cancel pending home warmups.
        manager.stopWarmup(event.getPlayer());
    }

    @EventHandler
    public void handleMove(PlayerMoveEvent event) {
        if (event.isCancelled())
            return;

        // Skip if player is only moving his mouse.
        if (event.getFrom().getX() == event.getTo().getX() &&
            event.getFrom().getY() == event.getTo().getY() &&
            event.getFrom().getZ() == event.getTo().getZ())
            return;

        manager.stopWarmup(event.getPlayer());
    }

    @EventHandler
    public void handleDamage(EntityDamageEvent event) {
        if (event.isCancelled())
            return;
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        manager.stopWarmup(player);
    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent event) {
        manager.stopWarmup(event.getPlayer());
    }

}
