package de.banarnia.fancyhomes.lang;

import de.banarnia.fancyhomes.api.lang.ILanguage;

public enum Message implements ILanguage {
    PREFIX("§8[§6FancyHomes§8]§7"),
    COMMAND_ERROR_CONSOLE_NOT_SUPPORTED("This command may not be executed by console."),
    COMMAND_ERROR_HOME_NOT_FOUND("Could not find a home with the name %home%."),

    COMMAND_ERROR_SETHOME_LIMIT_REACHED("%prefix% §cYou don't have any homes left."),
    COMMAND_ERROR_SETHOME_LIMIT_EXCEEDED("%prefix% §cYou can't edit any homes because you exceeded the home limit."),
    COMMAND_INFO_SETHOME_RELOCATED("%prefix% You relocated your home §e%home% §7to your current location."),
    COMMAND_INFO_SETHOME_CREATED("%prefix% Successfully created a new home §e%home%§7."),
    COMMAND_INFO_DELHOME_SUCCESS("%prefix% Successfully deleted your home §e%home%§7."),
    COMMAND_ERROR_HOME_LIMIT_EXCEEDED("%prefix% §cYou can't use any homes because you exceeded the home limit."),

    COMMAND_ERROR_HOME_COOLDOWN("%prefix% §cYou can't do that again within the next §e%time%s§c."),
    COMMAND_ERROR_HOME_WARMUP_ABORT("%prefix% §eTeleport aborted..."),
    COMMAND_INFO_HOME_WARMUP_STARTED("%prefix% You will be teleported in §e%time%s§7."),
    COMMAND_INFO_HOME_TELEPORT("%prefix% Teleporting to §e%home%§7..."),
    COMMAND_ERROR_HOME_NOT_SPECIFIED("%prefix% §cYou need to specify a home name."),
    COMMAND_ERROR_HOME_LOCATION_NOT_LOADED("%prefix% §cYou can't teleport to this home because the world is not loaded.")

    ;

    String defaultMessage, message;

    Message(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getKey() {
        return this.toString().toLowerCase().replace("_", "-");
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public String get() {
        String message = this.message != null ? this.message : defaultMessage;
        if (this == PREFIX)
            return message;

        return message.replace("%prefix%", PREFIX.get());
    }

    @Override
    public void set(String message) {
        this.message = message;
    }
}
