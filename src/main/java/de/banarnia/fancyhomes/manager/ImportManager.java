package de.banarnia.fancyhomes.manager;

import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.api.config.Config;
import de.banarnia.fancyhomes.api.config.YamlConfig;
import de.banarnia.fancyhomes.data.HomeData;
import de.banarnia.fancyhomes.data.storage.Home;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ImportManager {

    private FancyHomes plugin;
    private HomeManager manager;

    public ImportManager(FancyHomes plugin, HomeManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public ImportStats importExternalHomes(ImportSource importSource) {
        if (importSource == null)
            return null;

        switch (importSource) {
            case Essentials:
                return importFromEssentials();

            default:
                ImportStats stats = new ImportStats();
                stats.setError(true);
                stats.setErrorMessage("§c" + importSource.name() + " is not implemented yet.");
                return stats;
        }
    }

    private ImportStats importFromEssentials() {
        File playerDataFolder = new File("plugins" + File.separator + "Essentials" + File.separator + "userdata");
        ImportStats importStats = new ImportStats();

        if (!playerDataFolder.exists()) {
            String message = "§cFailed to import Home-Data from Essentials: Could'nt find playerdata in " + playerDataFolder.getPath();
            plugin.getLogger().warning(message);
            importStats.setError(true);
            importStats.setErrorMessage(message);
            return importStats;
        }

        Arrays.stream(playerDataFolder.listFiles()).filter(file -> file.getName().endsWith(".yml")).forEach(file -> {
            Config config = YamlConfig.of(file);
            if (!config.isSet("homes"))
                return;

            UUID playerId = UUID.fromString(file.getName().replace(".yml", ""));
            if (playerId == null)
                return;

            HomeData data = FancyHomesAPI.get().getHomeData(playerId).join();

            ConfigurationSection section = config.getConfigurationSection("homes");
            for (String key : section.getKeys(false)) {
                String world = section.getString(key + ".world-name");
                double x = section.getDouble(key + ".x");
                double y = section.getDouble(key + ".y");
                double z = section.getDouble(key + ".z");
                float yaw = (float) section.getDouble(key + ".yaw");
                float pitch = (float) section.getDouble(key + ".pitch");

                Home home = new Home(key, System.currentTimeMillis(), world, x, y, z, yaw, pitch);
                boolean success = data.addHome(home).join();
                if (success)
                    importStats.addSuccessfulImport(playerId);
                else
                    importStats.addFailedImport(playerId);
            }

            Player player = Bukkit.getPlayer(playerId);
            if (player == null || !player.isOnline())
                manager.unloadPlayer(playerId);
        });

        return importStats;
    }

}
