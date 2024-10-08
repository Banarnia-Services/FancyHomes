package de.banarnia.fancyhomes.events;

import de.banarnia.api.events.BanarniaEvent;
import de.banarnia.fancyhomes.data.HomeData;
import de.banarnia.fancyhomes.data.storage.Home;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.UUID;

public class HomeEvent extends BanarniaEvent implements Cancellable {

    private final Player player;
    private final UUID homeOwner;
    private final HomeData homeData;
    private final Home home;

    private boolean cancelled;

    public HomeEvent(Player player, Home home, HomeData homeData) {
        this.player = player;
        this.homeOwner = homeData.getUuid();
        this.home = home;
        this.homeData = homeData;
    }

    /**
     * Check if the teleported player is the owner of the home.
     * @return True if the player is the homeowner, else false.
     */
    public boolean isHomeOwner() {
        return player.getUniqueId().equals(homeOwner);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public HomeData getHomeData() {
        return homeData;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getHomeOwner() {
        return homeOwner;
    }

    public Home getHome() {
        return home;
    }
}
