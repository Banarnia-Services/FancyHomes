package de.banarnia.fancyhomes.api.config;

import de.banarnia.fancyhomes.api.UtilFile;
import de.banarnia.fancyhomes.api.UtilVersion;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

/**
 * YamlConfig that adds a version control.
 * This will add new config options within newer version, but keep the old configurations.
 * Keep in mind that ConfigurationSections will be overridden! You won't be able to add options to
 * existing ConfigurationSections, so keep everything in the root section that should be configured.
 */
public class YamlVersionConfig extends YamlConfig {

    @Getter
    protected String resourcePath;

    @Getter
    protected String version;

    private JavaPlugin plugin;

    /**
     * Creates a YamlVersionConfig.
     * @param plugin Plugin the resource belongs to.
     * @param file File to store the config in.
     * @param resourcePath Path to the resource.
     * @param version Config version that should be used.
     * @return New YamlVersionConfig.
     */
    public static YamlVersionConfig of(JavaPlugin plugin, File file, @NonNull String resourcePath, @NonNull String version) {
        YamlVersionConfig config = new YamlVersionConfig(plugin, file, resourcePath, version);
        config.loadConfig();

        return config;
    }

    /**
     * Creates a YamlVersionConfig.
     * @param plugin Plugin the resource belongs to.
     * @param folder File instance of the folder.
     * @param fileName Name of the file.
     * @param resourcePath Path to the resource.
     * @param version Config version that should be used.
     * @return New YamlVersionConfig.
     */
    public static YamlVersionConfig of(JavaPlugin plugin, File folder, String fileName, @NonNull String resourcePath, @NonNull String version) {
        return of(plugin, new File(folder, fileName), resourcePath, version);
    }

    /**
     * Creates a YamlVersionConfig.
     * @param plugin Plugin the resource belongs to.
     * @param folderPath Path to the folder.
     * @param fileName Name of the file.
     * @param resourcePath Path to the resource.
     * @param version Config version that should be used.
     * @return New YamlVersionConfig.
     */
    public static YamlVersionConfig of(JavaPlugin plugin, String folderPath, String fileName, @NonNull String resourcePath, @NonNull String version) {
        return of(plugin, new File(folderPath), fileName, resourcePath, version);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    protected YamlVersionConfig() {

    }

    /**
     * Constructor.
     * @param file File instance for the config.
     * @param resourcePath Path to the default config in the .jar file.
     * @param version Version of the config.
     */
    protected YamlVersionConfig(JavaPlugin plugin, File file, @NonNull String resourcePath, @NonNull String version) {
        super(file);
        this.plugin = plugin;
        this.resourcePath = resourcePath;
        this.version = version;

        if (!UtilVersion.isValidVersion(version))
            throw new IllegalArgumentException("Illegal version for a config: " + version);
    }

    /**
     * Loads a config and handles the version control
     * @return True if config was successfully loaded.
     */
    @Override
    public boolean loadConfig() {
        // Load config.
        super.loadConfig();

        // Map for existing entries.
        Map<String, Object> entries = null;

        // Read version.
        String version = getString("version");

        // If version is valid compare to base version.
        if (UtilVersion.isValidVersion(version) && !UtilVersion.isLower(version, this.version))
            return true;

        // Save all configured options in a map.
        entries = getValues(false);
        entries.remove("version");

        // Delete file.
        file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Copy resource.
        try {
            UtilFile.copyResource(plugin, resourcePath, file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to copy resource: " + resourcePath, e);
            return false;
        }

        // Load config.
        super.loadConfig();

        // Write prev values to config.
        for (Map.Entry<String, Object> entry : entries.entrySet())
            set(entry.getKey(), entry.getValue());

        // Save.
        return save();
    }
}