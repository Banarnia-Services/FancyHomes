package de.banarnia.fancyhomes.api.events;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class BanarniaEvent extends Event {

    /**
     * Call the event.
     */
    public void callEvent() {
        if (!Bukkit.isPrimaryThread())
            Bukkit.getLogger().warning("Calling event from async thread may not work: " + this.getClass().getName());

        Bukkit.getPluginManager().callEvent(this);
    }

    // Bukkit shit.
    @Getter
    private static final HandlerList handlers = new HandlerList();

    public static final HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
