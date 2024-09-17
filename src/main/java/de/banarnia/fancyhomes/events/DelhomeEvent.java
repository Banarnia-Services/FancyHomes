package de.banarnia.fancyhomes.events;

import de.banarnia.fancyhomes.api.events.BanarniaEvent;
import de.banarnia.fancyhomes.data.HomeData;
import de.banarnia.fancyhomes.data.storage.Home;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.UUID;

public class DelhomeEvent extends BanarniaEvent implements Cancellable {

    @Getter
    private final Player player;
    @Getter
    private final UUID homeOwner;
    @Getter
    private final HomeData homeData;
    @Getter
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
}
