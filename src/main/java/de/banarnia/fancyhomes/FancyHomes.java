package de.banarnia.fancyhomes;

import co.aikar.commands.BukkitCommandManager;
import de.banarnia.fancyhomes.api.config.Config;
import de.banarnia.fancyhomes.api.config.YamlVersionConfig;
import de.banarnia.fancyhomes.commands.HomeCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyHomes extends JavaPlugin {

    private HomeConfig homeConfig;

    @Override
    public void onEnable() {
        super.onEnable();

        BukkitCommandManager manager = new BukkitCommandManager(this);
        manager.usePerIssuerLocale(true);

        Config config = YamlVersionConfig.of(this, getDataFolder(), "config.yml",
                                    "config.yml", "1.0");
        this.homeConfig = new HomeConfig(config);

    }
}
