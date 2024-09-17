package de.banarnia.fancyhomes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.api.UtilThread;
import de.banarnia.fancyhomes.config.HomeConfig;
import de.banarnia.fancyhomes.data.storage.Home;
import de.banarnia.fancyhomes.lang.Message;
import de.banarnia.fancyhomes.manager.ImportManager;
import de.banarnia.fancyhomes.manager.ImportSource;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@CommandAlias("home")
public class HomeCommand extends BaseCommand {

    private final FancyHomesAPI api = FancyHomesAPI.get();
    private final HomeConfig config;
    private final ImportManager importManager;

    public HomeCommand(HomeConfig config, ImportManager importManager) {
        this.config = config;
        this.importManager = importManager;
    }

    @Default
    @CommandCompletion("@homes")
    public void home(Player sender, @Optional Home home) {
        if (home != null) {
            api.teleport(sender, home);
            return;
        }

        api.getHomeMap(sender.getUniqueId()).thenAccept(map -> {
            Home targetHome = map.get("Default");
            if (targetHome == null && map.size() == 1)
                targetHome = map.values().stream().findFirst().get();

            if (targetHome == null) {
                String message = map.isEmpty() ?
                        Message.COMMAND_ERROR_HOME_EMPTY.get() :
                        Message.COMMAND_ERROR_HOME_NOT_SPECIFIED.get();
                sender.sendMessage(message);
                return;
            }

            api.teleport(sender, targetHome);
        });
    }

    @Subcommand("others")
    @CommandPermission("fancyhomes.others")
    @CommandCompletion("@players @nothing")
    public void otherHome(Player sender, OfflinePlayer target, @Optional String homeName) {
        String finalHomeName = homeName != null ? homeName : "Default";
        api.getHome(target.getUniqueId(), finalHomeName)
                .thenAccept(home -> {
                    if (home == null) {
                        sender.sendMessage(Message.COMMAND_ERROR_HOME_OTHERS_NOT_FOUND
                                .replace("%home%", homeName)
                                .replace("%player%", target.getName()));
                        return;
                    }

                    api.teleport(sender, home);
                });
    }

    @Subcommand("reload")
    @CommandPermission("fancyhomes.reload")
    public void reload(CommandIssuer sender) {
        config.reload();
        sender.sendMessage(Message.COMMAND_INFO_HOME_CONFIG_RELOADED.get());
    }

    @Subcommand("import")
    @CommandPermission("fancyhomes.import")
    @CommandCompletion("@importSource")
    public void importExternalHomes(CommandIssuer sender, ImportSource importSource) {
        if (importSource == null)
            return;

        sender.sendMessage(Message.COMMAND_INFO_HOME_IMPORT_STARTED.replace("%source%", importSource.name()));

        CompletableFuture.supplyAsync(() -> importManager.importExternalHomes(importSource))
                .thenAccept(stats -> {
                    UtilThread.runSync(FancyHomes.getInstance(), () -> {
                        sender.sendMessage("§6Import finished");
                        sender.sendMessage("§7Successful Imports: §a" + stats.getOverallSuccessfulImports());
                        sender.sendMessage("§7Failed Imports: §c" + stats.getOverallFailedImports());
                        sender.sendMessage("§7Errors: §e" + stats.isError());
                        if (stats.isError())
                            sender.sendMessage(stats.getErrorMessage());

                        return stats.isError();
                    });
                });
    }

}
