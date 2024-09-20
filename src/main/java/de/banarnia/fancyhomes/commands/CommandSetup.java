package de.banarnia.fancyhomes.commands;

import de.banarnia.api.acf.CommandManager;
import de.banarnia.api.acf.InvalidCommandArgument;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.data.storage.Home;
import de.banarnia.fancyhomes.lang.Message;
import de.banarnia.fancyhomes.manager.ImportSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandSetup {

    public static void initCommandCompletion(CommandManager manager) {
        manager.getCommandCompletions().registerCompletion("homes", c -> {
            if (c.getIssuer().isPlayer())
                return FancyHomesAPI.get().getHomeNames(c.getIssuer().getUniqueId()).join();

            return new ArrayList<>();
        });
        manager.getCommandCompletions().registerCompletion("importSource",
                c -> Arrays.stream(ImportSource.values()).map(ImportSource::name).collect(Collectors.toList()));
    }

    public static void initCommandContext(CommandManager manager) {
        manager.getCommandContexts().registerContext(Home.class, c -> {
            if (!c.getIssuer().isPlayer())
                throw new InvalidCommandArgument(Message.COMMAND_ERROR_CONSOLE_NOT_SUPPORTED.get());

            String tag = c.popFirstArg();
            Home home = FancyHomesAPI.get().getHome(c.getIssuer().getUniqueId(), tag).join();
            if (home != null)
                return home;
            else
                throw new InvalidCommandArgument(Message.COMMAND_ERROR_HOME_NOT_FOUND.replace("%home%", tag));
        });

        manager.getCommandContexts().registerContext(ImportSource.class, c -> {
            String tag = c.popFirstArg();
            ImportSource source = null;
            for (ImportSource importSource : ImportSource.values())
                if (importSource.name().equalsIgnoreCase(tag))
                    source = importSource;

            if (source != null)
                return source;
            else
                throw new InvalidCommandArgument(Message.COMMAND_ERROR_HOME_IMPORT_INVALID_IMPORTSOURCE.get());
        });
    }

}
