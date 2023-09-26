package de.banarnia.fancyhomes.gui;

import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.api.UtilItem;
import de.banarnia.fancyhomes.api.UtilThread;
import de.banarnia.fancyhomes.data.HomeData;
import de.banarnia.fancyhomes.data.storage.Home;
import de.banarnia.fancyhomes.lang.Message;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.util.Legacy;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeGUI {

    private HomeData data;
    private OfflinePlayer target;
    private PaginatedGui gui;

    public HomeGUI(String title, HomeData data) {
        this.data = data;
        this.target = Bukkit.getOfflinePlayer(data.getUuid());
        this.gui = Gui.paginated().title(Legacy.SERIALIZER.deserialize(title)).rows(2).create();
        this.gui.setDefaultClickAction(event -> event.setCancelled(true));
        this.gui.setOpenGuiAction(event -> init());
    }

    private void init() {
        gui.getFiller().fillBottom(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(gui.getRows()-1, 4, getInfoItem());

        List<String> homeNamesSorted = data.getHomeMap().keySet().stream().sorted().collect(Collectors.toCollection(LinkedList::new));
        for (String homeName : homeNamesSorted) {
            Home home = data.getHome(homeName);
            gui.addItem(getHomeItem(home));
        }


    }

    private GuiItem getInfoItem() {
        ItemBuilder builder = ItemBuilder.from(UtilItem.getPlayerSkull(target));
        builder.setName("§e" + target.getName());

        int limit = FancyHomesAPI.get().getHomeLimit(target.getUniqueId());
        builder.setLore("§7Homes: §e" + data.getHomeMap().size() + (limit >= 0 ? "§7/§a" + limit : ""));

        return builder.asGuiItem();
    }

    private GuiItem getHomeItem(Home home) {
        GuiItem guiItem = new GuiItem(home.getIcon());
        guiItem.setAction(click -> {
            Player player = (Player) click.getWhoClicked();
            if (click.isLeftClick()) {
                FancyHomesAPI.get().teleport(player, home);
                return;
            }

            new ConfirmationGUI(Message.GUI_CONFIRMATION_TITLE.get(), delete -> {
                if (delete) {
                    FancyHomesAPI.get().deleteHome(player, target.getUniqueId(), home.getName())
                            .thenRun(() -> UtilThread.runSync(FancyHomes.getInstance(), () -> open(player)));
                } else
                    gui.open(player);
            }).open(player);
        });

        return guiItem;
    }

    public boolean open(Player player) {
        gui.open(player);
        return true;
    }

}
