package de.banarnia.fancyhomes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.data.Home;
import de.banarnia.fancyhomes.lang.Message;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@CommandAlias("delhome")
public class DelhomeCommand extends BaseCommand {

    private FancyHomesAPI api = FancyHomesAPI.get();

    @Default
    @CommandCompletion("@homes")
    public void delHome(Player sender, Home home) {
        CompletableFuture.runAsync(() -> api.deleteHome(sender, home.getName()))
                .thenRun(() -> sender.sendMessage(Message.COMMAND_INFO_DELHOME_SUCCESS.replace("%home%", home.getName())));


    }

}
