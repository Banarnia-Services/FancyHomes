package de.banarnia.fancyhomes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import co.aikar.locales.MessageKey;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.data.Home;
import de.banarnia.fancyhomes.lang.Message;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;

@CommandAlias("home")
@CommandPermission("fancyhomes.home")
public class HomeCommand extends BaseCommand {

    private FancyHomesAPI api = FancyHomesAPI.get();

    @Default
    @CommandCompletion("@homes")
    public void home(Player sender, @Optional Home home) {
        if (home == null) {
            HashMap<String, Home> homes = api.getHomes(sender);
            if (homes.size() == 1)
                home = homes.values().stream().findFirst().get();
            else {
                sender.sendMessage(Message.COMMAND_ERROR_HOME_NOT_SPECIFIED.get());
                return;
            }
        }

        if (!home.isLoaded()) {
            sender.sendMessage(Message.COMMAND_ERROR_HOME_LOCATION_NOT_LOADED.get());
            return;
        }

        sender.sendMessage(Message.COMMAND_INFO_HOME_TELEPORT.replace("%home%", home.getName()));
        home.teleport(sender, PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    @Default
    @CommandPermission("fancyhomes.others")
    @CommandCompletion("@players @nothing")
    public void otherHome(Player sender, OfflinePlayer target, @Optional String homeName) {
        // TODO: Implementation.
    }

}
