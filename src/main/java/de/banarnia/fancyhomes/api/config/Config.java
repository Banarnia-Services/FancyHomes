package de.banarnia.fancyhomes.api.config;

import java.util.List;
import java.util.Set;

/**
 * Interface to handle config actions.
 */
public interface Config {

    /**
     * Load the configuration from the given file.
     * @return True if it was loaded successfully, else false.
     */
    boolean loadConfig();

    /**
     * Save the current configuration to the given file.
     * @return True if it was saved successfully, else false.
     */
    boolean save();

    /**
     * Set a value in the config.
     * @param key Key to the value.
     * @param val Value.
     */
    void set(String key, Object val);

    /**
     * Get a Set containing all keys in the section.
     * @param deep Whether to get a deep list.
     * @return Set of all keys.
     */
    Set<String> getKeys(boolean deep);

    /**
     * Get a Set containing all keys in the section.
     * @param path Section.
     * @param deep Whether to get a deep list.
     * @return Set of all keys.
     */
    Set<String> getKeys(String path, boolean deep);

    /**
     * Check if a key is set in the config.
     * @param key Key in the config.
     * @return True if key is set, else false.
     */
    boolean isSet(String key);

    /**
     * Get a value from the config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Key from the config if it exists, else default value.
     */
    Object get(String key, Object def);

    /**
     * Get a value from the config.
     * @param key Key in the config.
     * @return Key from the config if it exists, else null value.
     */
    default Object get(String key) {
        return get(key, null);
    }

    /**
     * Get a value from the config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Key from the config if it exists, else default value.
     */
    String getString(String key, String def);

    /**
     * Get a value from the config.
     * @param key Key in the config.
     * @return Key from the config if it exists, else null value.
     */
    default String getString(String key) {
        return getString(key, null);
    }

    /**
     * Get a value from the config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Key from the config if it exists, else default value.
     */
    int getInt(String key, int def);

    /**
     * Get a value from the config.
     * @param key Key in the config.
     * @return Key from the config if it exists, else null value.
     */
    default int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * Get a value from the config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Key from the config if it exists, else default value.
     */
    double getDouble(String key, double def);

    /**
     * Get a value from the config.
     * @param key Key in the config.
     * @return Key from the config if it exists, else null value.
     */
    default double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    /**
     * Get a value from the config.
     * @param key Key in the config.
     * @return Key from the config if it exists, else null value.
     */
    List<String> getStringList(String key);
    /**
     * Get a value from the config.
     * @param key Key in the config.
     * @param def Default value.
     * @return Key from the config if it exists, else default value.
     */
    boolean getBoolean(String key, boolean def);

    /**
     * Get a value from the config.
     * @param key Key in the config.
     * @return Key from the config if it exists, else null value.
     */
    default boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * Get a value from the config or set a default value, if the key does not exist.
     * @param key Key in the config.
     * @param def Default value.
     * @param saveConfig Save config after writing the value.
     * @return Config value if it exists, else default value.
     * @param <T> Datatype of the value.
     */
    <T> T getOrElseSet(String key, T def, boolean saveConfig);

    /**
     * Get a value from the config or set a default value, if the key does not exist.
     * Config will be saved afterward.
     * @param key Key in the config.
     * @param def Default value.
     * @return Config value if it exists, else default value.
     * @param <T> Datatype of the value.
     */
    default <T> T getOrElseSet(String key, T def) {
        return getOrElseSet(key, def, true);
    }

}