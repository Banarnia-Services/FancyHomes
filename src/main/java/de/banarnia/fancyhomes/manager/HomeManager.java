package de.banarnia.fancyhomes.manager;

import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.api.permissions.PermissionManager;
import de.banarnia.api.sql.Database;
import de.banarnia.api.sql.MySQL;
import de.banarnia.fancyhomes.config.HomeConfig;
import de.banarnia.fancyhomes.data.HomeData;
import de.banarnia.fancyhomes.data.storage.*;
import de.banarnia.fancyhomes.events.HomeEvent;
import de.banarnia.fancyhomes.lang.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HomeManager implements FancyHomesAPI {

    private final FancyHomes plugin;
    private final HomeConfig config;

    private final HashMap<UUID, HomeData> cachedHomeData = new HashMap<>();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final HashMap<UUID, Integer> warmups = new HashMap<>();

    public HomeManager(FancyHomes plugin, HomeConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public boolean init() {
        StorageMethod method = config.getStorageMethod();
        if (method == StorageMethod.MySQL) {
            Database database = config.getDatabase();
            if (database == null || !database.openConnection(!config.debugMode())) {
                plugin.getLogger().warning("Failed to connect to MySQL server. Disabling the plugin...");
                plugin.getLogger().warning("Consider changing your storage method in the config.yml if you cannot provide a MySQL server.");
                return false;
            }
        }

        plugin.getLogger().info("Selected storage method: " + method.toString());
        return true;
    }

    public void startCooldown(Player player) {
        double cooldown = config.getCooldownTime();
        if (cooldown <= 0 || player.hasPermission("fancyhomes.cooldown.bypass"))
            return;

        cooldowns.put(player.getUniqueId(), (System.currentTimeMillis() + (long) (cooldown * 1000.0)));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> cooldowns.remove(player.getUniqueId()), (long) (20.0 * cooldown));
    }

    public void startWarmup(Player player, Home home) {
        stopWarmup(player);

        HomeEvent event = new HomeEvent(player, home, cachedHomeData.get(player.getUniqueId()));
        event.callEvent();
        if (event.isCancelled()) {
            player.sendMessage(Message.COMMAND_ERROR_HOME_CANCELED.get());
            return;
        }

        double warmup = config.getWarmupTime();
        if (warmup <= 0 || player.hasPermission("fancyhomes.warmup.bypass")) {
            home.teleport(player);
            startCooldown(player);
            return;
        }

        player.sendMessage(Message.COMMAND_INFO_HOME_WARMUP_STARTED.replace("%time%", String.valueOf((int)warmup)));

        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (player != null && player.isOnline())
                home.teleport(player);

            warmups.remove(player.getUniqueId());
            startCooldown(player);
        }, (long)(20.0 * warmup));
        warmups.put(player.getUniqueId(), taskId);
    }

    public void stopWarmup(Player player) {
        if (!warmups.containsKey(player.getUniqueId()))
            return;

        int taskId = warmups.get(player.getUniqueId());
        Bukkit.getScheduler().cancelTask(taskId);
        warmups.remove(player.getUniqueId());

        player.sendMessage(Message.COMMAND_ERROR_HOME_WARMUP_ABORT.get());
    }

    public CompletableFuture<HomeData> getHomeData(UUID playerId) {
        if (cachedHomeData.containsKey(playerId))
            return CompletableFuture.completedFuture(cachedHomeData.get(playerId));

        return CompletableFuture.supplyAsync(() -> {
            HomeStorage storage;
            switch (config.getStorageMethod()) {
                case MySQL:
                    storage = new HomeMySQLStorage(plugin, playerId, (MySQL) config.getDatabase());
                    break;
                default:
                    storage = new HomeFileStorage(plugin, playerId);
                    break;
            }

            storage.init().join();
            storage.loadHomesFromStorage().join();

            cachedHomeData.put(playerId, storage);
            return storage;
        });
    }

    @Override
    public int getHomeLimit(UUID playerId) {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            int configLimit = config.getMaxHomes();
            int userLimit = LuckPermsHook.getLuckPermsHomeLimit(playerId);
            return Math.max(configLimit, userLimit);
        }

        return config.getMaxHomes();
    }

    @Override
    public boolean hasCooldown(Player player) {
        return cooldowns.containsKey(player.getUniqueId());
    }

    @Override
    public boolean isInWarmup(Player player) {
        return warmups.containsKey(player.getUniqueId());
    }

    @Override
    public void teleport(Player player, Home home) {
        if (player == null || home == null)
            throw new IllegalArgumentException("You need to specify a player and a home.");

        if (homeLimitExceeded(player.getUniqueId()).join()) {
            player.sendMessage(Message.COMMAND_ERROR_HOME_LIMIT_EXCEEDED.get());
            return;
        }

        if (!home.isLoaded()) {
            player.sendMessage(Message.COMMAND_ERROR_HOME_LOCATION_NOT_LOADED.get());
            return;
        }

        if (hasCooldown(player)) {
            long remainingSeconds = (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
            player.sendMessage(Message.COMMAND_ERROR_HOME_COOLDOWN.replace("%time%", String.valueOf(remainingSeconds)));
            return;
        }

        startWarmup(player, home);
    }

    public void unloadPlayer(UUID playerId) {
        this.cachedHomeData.remove(playerId);
    }
}
