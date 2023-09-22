package de.banarnia.fancyhomes.events;

import de.banarnia.fancyhomes.api.events.BanarniaEvent;
import de.banarnia.fancyhomes.data.storage.Home;
import de.banarnia.fancyhomes.data.HomeData;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.UUID;

public class DelhomeEvent extends BanarniaEvent implements Cancellable {

    @Getter
    private Player player;
    @Getter
    private UUID homeOwner;
    @Getter
    private HomeData homeData;
    @Getter
    private Home home;

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
}
