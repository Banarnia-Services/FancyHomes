package de.banarnia.fancyhomes.api.lang;

import de.banarnia.fancyhomes.api.config.Config;
import de.banarnia.fancyhomes.api.config.YamlConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class handles message translation used in the plugin within given files.
 */
public class LanguageHandler {

    private JavaPlugin plugin;
    private Config config;

    private List<Class<? extends ILanguage>> registeredEnums = new ArrayList<>();

    /**
     * Default constructor.
     * @param plugin Corresponding plugin.
     * @param lang Language.
     */
    public LanguageHandler(JavaPlugin plugin, String lang) {
        lang = lang.replace(".yml", "").replace(".yaml", "");
        this.plugin = plugin;
        this.config = YamlConfig.of(plugin.getDataFolder().getPath() + File.separator + "lang", lang + ".yml");
    }

    /**
     * Check if a message enum is registered.
     * @param enumClass Class to check.
     * @return True if it is registered, else false.
     */
    public boolean isRegistered(Class<? extends ILanguage> enumClass) {
        return registeredEnums != null ? registeredEnums.contains(enumClass) : false;
    }

    /**
     * Register a message enum.
     * @param enumeration Class to register.
     */
    public void register(Class<? extends ILanguage> enumeration) {
        register(enumeration, config);
    }

    /**
     * Register a message enum with a specific config.
     * @param enumeration Class to register.
     * @param config File to store the translation in.
     */
    public void register(Class<? extends ILanguage> enumeration, Config config) {
        // Null checks.
        if (enumeration == null || config == null || registeredEnums == null || !enumeration.isEnum())
            throw new IllegalArgumentException();

        // Check if enum is already registered.
        if (isRegistered(enumeration))
            throw new IllegalArgumentException("Language enum is already registered: " + enumeration.getName());

        // Insert enum into the map.
        registeredEnums.add(enumeration);

        // Load enum.
        load(enumeration);
    }

    /**
     * Load a message enum.
     * @param enumClass Registered enum class.
     */
    public void load(Class<? extends ILanguage> enumClass) {
        // Null check.
        if (enumClass == null)
            throw new IllegalArgumentException();

        // Check if enum is registered.
        if (!isRegistered(enumClass))
            throw new IllegalArgumentException("Language enum is not registered: " + enumClass.getName());

        // Reload config.
        config.loadConfig();

        // Read values.
        Arrays.stream(enumClass.getEnumConstants()).forEach(enumValue -> {
            // Key to the message.
            String key = enumClass.getSimpleName() + "." + enumValue.getKey();

            // Default message.
            String defaultMessage = enumValue.getDefaultMessage();

            // Message in the config.
            String message = config.getOrElseSet(key, defaultMessage);

            // Set message in enum.
            enumValue.set(message);
        });
    }

    /**
     * Reload all messages.
     */
    public void reload() {
        registeredEnums.forEach(enumeration -> load(enumeration));
    }
}