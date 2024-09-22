package de.banarnia.fancyhomes;

import de.banarnia.api.acf.BukkitCommandManager;
import de.banarnia.api.acf.CommandManager;
import de.banarnia.api.config.Config;
import de.banarnia.api.config.YamlConfig;
import de.banarnia.api.config.YamlVersionConfig;
import de.banarnia.api.lang.LanguageHandler;
import de.banarnia.fancyhomes.commands.*;
import de.banarnia.fancyhomes.config.HomeConfig;
import de.banarnia.fancyhomes.data.storage.Home;
import de.banarnia.fancyhomes.lang.Message;
import de.banarnia.fancyhomes.listener.HomeListener;
import de.banarnia.fancyhomes.manager.HomeManager;
import de.banarnia.fancyhomes.manager.ImportManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class FancyHomes extends JavaPlugin {

    private static FancyHomes instance;

    private CommandManager commandManager;
    private HomeConfig homeConfig;
    private LanguageHandler languageHandler;
    private HomeManager manager;
    private ImportManager importManager;

    @Override
    public void onLoad() {
        super.onLoad();
        instance = this;

        ConfigurationSerialization.registerClass(Home.class);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        // BStats
        int pluginId = 19868;
        Metrics metrics = new Metrics(this, pluginId);

        this.commandManager = new BukkitCommandManager(this);
        commandManager.usePerIssuerLocale(true);

        Config config = YamlVersionConfig.of(this, getDataFolder(), "config.yml",
                                    "config.yml", "1.0");
        this.homeConfig = new HomeConfig(this, config);

        File langFolder = new File(getDataFolder(), "lang");
        YamlConfig.fromResource(this, "lang/en.yml", langFolder, "en.yml");
        YamlConfig.fromResource(this, "lang/de.yml", langFolder, "de.yml");
        this.languageHandler = new LanguageHandler(this, homeConfig.getLanguage());
        this.languageHandler.register(Message.class);

        this.manager = new HomeManager(this, homeConfig);
        if (!this.manager.init()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.importManager = new ImportManager(this, manager);

        Bukkit.getPluginManager().registerEvents(new HomeListener(manager), this);

        CommandSetup.initCommandCompletion(commandManager);
        CommandSetup.initCommandContext(commandManager);

        commandManager.registerCommand(new HomeCommand(homeConfig, importManager));
        commandManager.registerCommand(new HomesCommand());
        commandManager.registerCommand(new SethomeCommand());
        commandManager.registerCommand(new DelhomeCommand());

        Bukkit.getOnlinePlayers().forEach(player -> manager.getHomeData(player.getUniqueId()));

        // Add metrics.
        metrics.addCustomChart(new SimplePie("storage_method", () -> homeConfig.getStorageMethod().toString()));
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(Player::closeInventory);
    }

    public static FancyHomes getInstance() {
        return instance;
    }

    public HomeManager getManager() {
        return manager;
    }
}
