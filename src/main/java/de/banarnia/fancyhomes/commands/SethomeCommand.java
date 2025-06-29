package de.banarnia.fancyhomes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.banarnia.fancyhomes.FancyHomesAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("sethome")
public class SethomeCommand extends BaseCommand {

    private final FancyHomesAPI api = FancyHomesAPI.get();

    @Default
    @CommandCompletion("@homes")
    public void setHome(Player sender, @Optional @Single String homeName) {
        api.addHome(sender, sender.getUniqueId(), homeName, sender.getLocation());
    }

    @Subcommand("others")
    @CommandPermission("fancyhomes.others")
    @CommandCompletion("@players @nothing")
    public void setHome(Player sender, OfflinePlayer target, @Optional @Single String homeName) {
        api.addHome(sender, target.getUniqueId(), homeName, sender.getLocation());
    }

}
