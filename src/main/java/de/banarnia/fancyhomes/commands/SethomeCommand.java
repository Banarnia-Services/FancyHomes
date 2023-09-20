package de.banarnia.fancyhomes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.lang.Message;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@CommandAlias("sethome")
public class SethomeCommand extends BaseCommand {

    private FancyHomesAPI api = FancyHomesAPI.get();

    @Default
    public void setHome(Player sender, @Optional @Single String homeName) {
        if (homeName == null)
            homeName = "Default";

        if (api.homeLimitExceeded(sender)) {
            sender.sendMessage(Message.COMMAND_ERROR_SETHOME_LIMIT_EXCEEDED.get());
            return;
        }

        String finalHomeName = homeName;
        if (api.hasHome(sender, homeName)) {
            CompletableFuture.runAsync(() -> {
                api.deleteHome(sender, finalHomeName);
                api.addHome(sender, finalHomeName, sender.getLocation());
            }).thenRun(() -> sender.sendMessage(Message.COMMAND_INFO_SETHOME_RELOCATED.replace("%home%", finalHomeName)));
            return;
        }

        if (api.homeLimitReached(sender)) {
            sender.sendMessage(Message.COMMAND_ERROR_SETHOME_LIMIT_REACHED.get());
            return;
        }

        CompletableFuture.runAsync(() -> {
            api.addHome(sender, finalHomeName, sender.getLocation());
        })
                .thenRun(() -> sender.sendMessage(Message.COMMAND_INFO_SETHOME_CREATED.replace("%home%", finalHomeName)));
    }

}
