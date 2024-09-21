package de.banarnia.fancyhomes.events;

import de.banarnia.api.events.BanarniaEvent;
import de.banarnia.fancyhomes.data.HomeData;
import de.banarnia.fancyhomes.data.storage.Home;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.UUID;

public class DelhomeEvent extends BanarniaEvent implements Cancellable {

    private final Player player;
    private final UUID homeOwner;
    private final HomeData homeData;
    private final Home home;

    private boolean cancelled;

    public DelhomeEvent(Player player, HomeData homeData, Home home) {
        this.player = player;
        this.homeOwner = homeData.getUuid();
        this.homeData = homeData;
        this.home = home;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public Home getHome() {
        return home;
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

}
