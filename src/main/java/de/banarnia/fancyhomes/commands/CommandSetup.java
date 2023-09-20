package de.banarnia.fancyhomes.commands;

import co.aikar.commands.CommandManager;
import co.aikar.commands.InvalidCommandArgument;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.data.Home;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandSetup {

    public static void initCommandCompletion(CommandManager manager) {
        manager.getCommandCompletions().registerCompletion("homes", c -> {
            if (c.getIssuer().isPlayer())
                return FancyHomesAPI.get().getHomes((Player) c.getIssuer()).keySet();

            return new ArrayList<>();
        });
    }

    public static void initCommandContext(CommandManager manager) {
        manager.getCommandContexts().registerContext(Home.class, c -> {
            if (!c.getIssuer().isPlayer())
                // TODO: Message.
                throw new InvalidCommandArgument();

            String tag = c.popFirstArg();
            Home home = FancyHomesAPI.get().getHome((Player) c.getIssuer(), tag);
            if (home != null)
                return home;
            else
                // TODO: Message.
                throw new InvalidCommandArgument();
        });
    }

}
