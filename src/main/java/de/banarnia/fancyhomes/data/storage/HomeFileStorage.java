package de.banarnia.fancyhomes.data.storage;

import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.api.config.Config;
import de.banarnia.fancyhomes.api.config.YamlConfig;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HomeFileStorage extends HomeStorage {

    private Config config;

    public HomeFileStorage(FancyHomes plugin, UUID playerId) {
        super(plugin, playerId);
    }

    @Override
    public CompletableFuture<Boolean> init() {
        File folder = new File(plugin.getDataFolder(), "data");
        folder.mkdirs();

        this.config = YamlConfig.of(folder, playerId.toString() + ".yml");
        config.set("name", getPlayerName());
        config.save();

        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> loadHomesFromStorage() {
        return CompletableFuture.supplyAsync(() -> {
            ConfigurationSection section = config.getConfigurationSection("homes");
            if (section == null)
                return false;

            for (String key : section.getKeys(false)) {
                Home home = section.getSerializable(key, Home.class);
                homes.put(home.getName(), home);
            }

            return true;
        });
    }

    @Override
    protected CompletableFuture<Boolean> saveHomeInStorage(Home home) {
        return CompletableFuture.supplyAsync(() -> {
            config.set("homes." + convertKey(home.getName()), home);
            config.save();

            return true;
        });
    }

    @Override
    protected CompletableFuture<Boolean> deleteHomeFromStorage(String homeName) {
        return CompletableFuture.supplyAsync(() -> {
            String homeKey = convertKey(homeName);
            if (!config.isSet("homes." + homeKey))
                return false;

            config.set("homes." + homeKey, null);
            config.save();
            return true;
        });
    }

    @Override
    protected CompletableFuture<Boolean> updateHomeLocationInStorage(String homeName, Location location, long timestamp) {
        Home home = getHome(homeName);
        home.updateLocation(location, System.currentTimeMillis());

        return saveHomeInStorage(home);
    }

    @Override
    protected CompletableFuture<Boolean> updateHomeIconInStorage(String homeName, String newIcon) {
        Home home = getHome(homeName);
        home.setIcon(newIcon);

        return saveHomeInStorage(home);
    }

    /**
     * Remove all dots in home name to enable a correct save in the yaml file.
     * @param key Key to convert.
     * @return Key without dots.
     */
    private String convertKey(String key) {
        return key.replace('.', '_');
    }
}
