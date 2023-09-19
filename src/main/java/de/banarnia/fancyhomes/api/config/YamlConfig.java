package de.banarnia.fancyhomes.api.config;

import de.banarnia.fancyhomes.api.UtilFile;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

/**
 * Extension of the bukkit YamlConfiguration for faster access.
 */
public class YamlConfig extends YamlConfiguration implements Config {

    @Getter
    protected File file;

    /**
     * Creates a YamlConfig.
     * @param file File to store the config in.
     * @return New YamlConfig.
     */
    public static YamlConfig of(File file) {
        YamlConfig config = new YamlConfig(file);
        config.loadConfig();

        return config;
    }

    /**
     * Creates a YamlConfig.
     * @param folder File instance of the folder.
     * @param fileName Name of the file.
     * @return New YamlConfig.
     */
    public static YamlConfig of(File folder, String fileName) {
        return of(new File(folder, fileName));
    }

    /**
     * Creates a YamlConfig.
     * @param folderPath Path to the folder.
     * @param fileName Name of the file.
     * @return New YamlConfig.
     */
    public static YamlConfig of(String folderPath, String fileName) {
        return of(new File(folderPath), fileName);
    }

    /**
     * Creates a YamlConfig.
     * @param file File to store the config in.
     * @return New YamlConfig.
     */
    public static YamlConfig fromResource(JavaPlugin plugin, String resourcePath, File file) {
        YamlConfig config = new YamlConfig(file);

        // Copy resource if file does not exist.
        if (!file.exists()) {
            // Load config to create file.
            config.loadConfig();

            // Try to copy default resource.
            try {
                UtilFile.copyResource(plugin, resourcePath, file);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to copy resource: " + resourcePath, e);
            }
        }

        // Load config afterward.
        config.loadConfig();

        return config;
    }

    /**
     * Creates a YamlConfig.
     * @param folder File instance of the folder.
     * @param fileName Name of the file.
     * @return New YamlConfig.
     */
    public static YamlConfig fromResource(JavaPlugin plugin, String resourcePath, File folder, String fileName) {
        return fromResource(plugin, resourcePath, new File(folder, fileName));
    }

    /**
     * Creates a YamlConfig.
     * @param folderPath Path to the folder.
     * @param fileName Name of the file.
     * @return New YamlConfig.
     */
    public static YamlConfig fromResource(JavaPlugin plugin, String resourcePath, String folderPath, String fileName) {
        return fromResource(plugin, resourcePath, new File(folderPath), fileName);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    protected YamlConfig() {

    }

    protected YamlConfig(File file) {
        this.file = file;
    }

    /**
     * Loads the config from the given file.
     * @return True if it was loaded successfully, else false.
     */
    public boolean loadConfig() {
        // Create file if it does not exist.
        if (!this.file.exists() || this.file.isDirectory()) {
            // Create parent folders.
            this.file.getParentFile().mkdirs();

            try {
                this.file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Load config.
        try {
            load(this.file);
        } catch (IOException|InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Saves the config to the given file.
     * @return True if it was saved successfully, else false.
     */
    public boolean save() {
        // Save config.
        try {
            this.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Get a specific key from the config or set a default value if the key does not exist.
     * @param key Key in the config.
     * @param def Default value.
     * @param saveConfig Save config after writing the value.
     * @return Config value, if exists, else default value.
     * @param <T> Datatype of the value.
     */
    public <T> T getOrElseSet(String key, T def, boolean saveConfig) {
        // Check if key exists.
        if (isSet(key))
            return (T) get(key);

        // Set value in config.
        set(key, def);

        // Save config.
        if (saveConfig)
            save();

        return def;
    }

    @Override
    public Set<String> getKeys(String path, boolean deep) {
        ConfigurationSection section = getConfigurationSection(path);
        if (section == null)
            return Set.of();

        return section.getKeys(deep);
    }

}