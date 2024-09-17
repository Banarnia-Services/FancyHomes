package de.banarnia.fancyhomes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.api.UtilThread;
import de.banarnia.fancyhomes.data.HomeData;
import de.banarnia.fancyhomes.gui.HomeGUI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("homes")
public class HomesCommand extends BaseCommand {

    private final FancyHomesAPI api = FancyHomesAPI.get();

    @Default
    public void openGui(Player sender) {
        HomeData data = FancyHomesAPI.get().getHomeData(sender.getUniqueId()).join();
        new HomeGUI("§fHomes: §e" + sender.getName(), data).open(sender);
    }

    @Default
    @CommandPermission("fancyhomes.others")
    @CommandCompletion("@players")
    public void openGui(Player sender, OfflinePlayer target) {
        FancyHomesAPI.get().getHomeData(target.getUniqueId())
                .thenAccept(data -> UtilThread.runSync(FancyHomes.getInstance(),
                        () -> new HomeGUI("§fHomes: §e" + target.getName(), data).open(sender)));
    }

    @Subcommand("list")
    public void homesList(Player sender) {
        listOthersHomes(sender, sender);
    }

    @Subcommand("list")
    @CommandPermission("fancyhomes.others")
    @CommandCompletion("@players @nothing")
    public void listOthersHomes(Player sender, OfflinePlayer target) {
        api.listHomes(sender, target.getUniqueId());
    }

}
