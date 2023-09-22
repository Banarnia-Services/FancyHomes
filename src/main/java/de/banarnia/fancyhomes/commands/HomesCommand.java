package de.banarnia.fancyhomes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.banarnia.fancyhomes.FancyHomesAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("homes")
public class HomesCommand extends BaseCommand {

    private FancyHomesAPI api = FancyHomesAPI.get();

    @Default
    public void openGui(Player sender) {
        sender.sendMessage("Not yet implemented.");
    }

    @Subcommand("list")
    public void homesList(Player sender) {
        listOthersHomes(sender, sender);
    }

    @Subcommand("list")
    @CommandPermission("fancyhomes.others")
    @CommandCompletion("@players @nothing")
    public void listOthersHomes(Player sender, OfflinePlayer target) {
        api.listHomes(sender, target.getUniqueId());
    }

}
