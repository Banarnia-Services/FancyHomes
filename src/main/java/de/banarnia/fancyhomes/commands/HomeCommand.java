package de.banarnia.fancyhomes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import co.aikar.locales.MessageKey;
import de.banarnia.fancyhomes.data.Home;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("home")
@CommandPermission("fancyhomes.home")
public class HomeCommand extends BaseCommand {

    @Default
    @CommandCompletion("@homes")
    public void home(Player sender, @Optional Home home) {
        // TODO: Implementation.
    }

    @Default
    @CommandPermission("fancyhomes.others")
    @CommandCompletion("@players @nothing")
    public void otherHome(Player sender, OfflinePlayer target, @Optional String homeName) {
        // TODO: Implementation.
    }

}
