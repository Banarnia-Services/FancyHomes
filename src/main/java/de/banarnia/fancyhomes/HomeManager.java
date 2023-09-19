package de.banarnia.fancyhomes;

import de.banarnia.fancyhomes.data.HomeData;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HomeManager {

    private HashMap<UUID, HomeData> cachedPlayerData = new HashMap<>();

    public HomeManager() {

    }

    public CompletableFuture<HomeData> loadPlayer(UUID playerId) {
        if (isLoaded(playerId))
            return CompletableFuture.completedFuture(cachedPlayerData.get(playerId));

        // TODO: Implement player data loading.
        return null;
    }

    public HomeData getHomeData(UUID playerId) {
        return cachedPlayerData.get(playerId);
    }

    public boolean isLoaded(UUID playerId) {
        return cachedPlayerData.containsKey(playerId);
    }

    public void removeFromCache(UUID playerId) {
        this.cachedPlayerData.remove(playerId);
    }

}
