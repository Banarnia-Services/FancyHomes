package de.banarnia.fancyhomes.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("homes")
public class HomesCommand {

    @Default
    public void homes(Player sender) {
        // TODO: Implementation.
    }

    @Default
    @CommandPermission("fancyhomes.others")
    @CommandCompletion("@players @nothing")
    public void otherHomes(Player sender, OfflinePlayer target) {
        // TODO: Implementation.
    }

}
