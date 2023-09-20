package de.banarnia.fancyhomes;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandManager;
import de.banarnia.fancyhomes.api.config.Config;
import de.banarnia.fancyhomes.api.config.YamlVersionConfig;
import de.banarnia.fancyhomes.commands.CommandSetup;
import de.banarnia.fancyhomes.config.HomeConfig;
import de.banarnia.fancyhomes.manager.HomeManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyHomes extends JavaPlugin {

    private static FancyHomes instance;

    private CommandManager commandManager;
    private HomeConfig homeConfig;
    private HomeManager manager;

    @Override
    public void onLoad() {
        super.onLoad();
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.commandManager = new BukkitCommandManager(this);
        commandManager.usePerIssuerLocale(true);

        Config config = YamlVersionConfig.of(this, getDataFolder(), "config.yml",
                                    "config.yml", "1.0");
        this.homeConfig = new HomeConfig(this, config);
        this.manager = new HomeManager(this, homeConfig);

        CommandSetup.initCommandCompletion(commandManager);
        CommandSetup.initCommandContext(commandManager);
    }

    protected static FancyHomes getInstance() {
        return instance;
    }

    public HomeManager getManager() {
        return manager;
    }
}
