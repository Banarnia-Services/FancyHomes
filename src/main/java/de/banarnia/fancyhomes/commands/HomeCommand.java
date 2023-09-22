package de.banarnia.fancyhomes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.config.HomeConfig;
import de.banarnia.fancyhomes.data.storage.Home;
import de.banarnia.fancyhomes.lang.Message;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("home")
public class HomeCommand extends BaseCommand {

    private FancyHomesAPI api = FancyHomesAPI.get();
    private HomeConfig config;

    public HomeCommand(HomeConfig config) {
        this.config = config;
    }

    @Default
    @CommandCompletion("@homes")
    public void home(Player sender, @Optional Home home) {
        if (home != null)
            api.teleport(sender, home);

        api.getHomeMap(sender.getUniqueId()).thenAccept(map -> {
            Home targetHome = map.get("Default");
            if (targetHome == null && map.size() == 1)
                targetHome = map.values().stream().toList().get(0);

            if (targetHome == null) {
                String message = map.size() == 0 ?
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

}
