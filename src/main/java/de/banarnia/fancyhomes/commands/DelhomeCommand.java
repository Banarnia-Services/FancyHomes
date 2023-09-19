package de.banarnia.fancyhomes.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import de.banarnia.fancyhomes.data.Home;
import org.bukkit.entity.Player;

@CommandAlias("delhome")
public class DelhomeCommand {

    @Default
    @CommandCompletion("@homes")
    public void delHome(Player sender, Home home) {
        // TODO: Implementation.
    }

}
