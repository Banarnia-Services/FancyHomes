package de.banarnia.fancyhomes.api.lang;

public interface ILanguage {

    /**
     * Get the key in the config.
     * @return Config key.
     */
    String getKey();

    /**
     * Get the default message provided in the enum.
     * @return Default message.
     */
    String getDefaultMessage();

    /**
     * Get the current message if it is not null.
     * @return Currently configured message.
     */
    String get();

    /**
     * Get the current message and replace specific parts.
     * @param prev String to be replaced.
     * @param replacement Replacement.
     * @return Message with replaced parts.
     */
    default String replace(String prev, String replacement) {
        return get().replace(prev, replacement);
    }

    /**
     * Set the current message.
     * @param message Overriding message.
     */
    void set(String message);
}
