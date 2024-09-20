package de.banarnia.fancyhomes.commands;

import de.banarnia.api.acf.BaseCommand;
import de.banarnia.api.acf.annotation.*;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.data.storage.Home;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("delhome")
public class DelhomeCommand extends BaseCommand {

    private final FancyHomesAPI api = FancyHomesAPI.get();

    @Default
    @CommandCompletion("@homes")
    public void delHome(Player sender, Home home) {
        api.deleteHome(sender, sender.getUniqueId(), home.getName());
    }

    @Subcommand("others")
    @CommandPermission("fancyhomes.others")
    @CommandCompletion("@players @nothing")
    public void delHome(Player sender, OfflinePlayer target, String homeName) {
        api.deleteHome(sender, target.getUniqueId(), homeName);
    }

}
