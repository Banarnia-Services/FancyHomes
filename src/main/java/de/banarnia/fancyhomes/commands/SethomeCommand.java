package de.banarnia.fancyhomes.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import org.bukkit.entity.Player;

@CommandAlias("sethome")
public class SethomeCommand {

    @Default
    public void setHome(Player player, @Optional @Single String homeName) {
        // TODO: Implementation.
    }

}
