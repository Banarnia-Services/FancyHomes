package de.banarnia.fancyhomes.gui;

import de.banarnia.api.UtilItem;
import de.banarnia.api.UtilThread;
import de.banarnia.fancyhomes.api.UtilGUI;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.util.Legacy;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.data.HomeData;
import de.banarnia.fancyhomes.data.storage.Home;
import de.banarnia.fancyhomes.lang.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeGUI {

    private final HomeData data;
    private final OfflinePlayer target;
    private final PaginatedGui gui;

    public HomeGUI(String title, HomeData data) {
        this.data = data;
        this.target = Bukkit.getOfflinePlayer(data.getUuid());
        this.gui = Gui.paginated().title(Legacy.SERIALIZER.deserialize(title)).rows(2).pageSize(9).create();
        this.gui.setDefaultClickAction(event -> event.setCancelled(true));
        this.gui.setOpenGuiAction(event -> init());
    }

    private void init() {
        gui.clearPageItems();

        gui.getFiller().fillBottom(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" ").asGuiItem());
        gui.setItem(gui.getRows(), 5, getInfoItem());

        List<String> homeNamesSorted = data.getHomeMap().keySet().stream().sorted().collect(Collectors.toCollection(LinkedList::new));
        for (String homeName : homeNamesSorted) {
            Home home = data.getHome(homeName);
            gui.addItem(getHomeItem(home));
        }

        UtilGUI.setPaginationItems(gui, Message.GUI_HOME_PAGE_PREVIOUS.get(), Message.GUI_HOME_PAGE_NEXT.get());
        gui.update();
    }

    private GuiItem getInfoItem() {
        ItemBuilder builder = ItemBuilder.from(UtilItem.getPlayerSkull(target));
        builder.setName(Message.GUI_HOME_INFO_NAME.replace("%player%", target.getName()));

        String current = String.valueOf(data.getHomeMap().size());
        String limit = String.valueOf(FancyHomesAPI.get().getHomeLimit(target.getUniqueId()));
        String lore = Message.GUI_HOME_INFO_LORE
                .replace("%homes_current%", current)
                        .replace("%homes_limit%", limit);
        builder.setLore(lore.split("\n"));

        return builder.asGuiItem();
    }

    private GuiItem getHomeItem(Home home) {
        GuiItem guiItem = new GuiItem(buildHomeIcon(home));
        guiItem.setAction(click -> {
            Player player = (Player) click.getWhoClicked();
            if (click.isLeftClick()) {
                if (click.isShiftClick()) {
                    new MaterialSelectionGUI(Message.GUI_ICON_SELECTION_TITLE.get(), Material.getMaterial(home.getIcon()),
                            Message.GUI_SAVE_NAME.get(), Message.GUI_CANCEL_NAME.get(), material -> {
                        if (material == null || material.toString() == home.getIcon()) {
                            open(player);
                            return;
                        }

                        data.updateHome(home.getName(), material.toString())
                                .thenAccept(success -> UtilThread.runSync(FancyHomes.getInstance(), () -> {
                                    if (!success)
                                        player.sendMessage(Message.GUI_ICON_UPDATE_FAILED.get());

                                    return open(player);
                                }));
                    }).open(player);
                    return;
                }

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

    public ItemStack buildHomeIcon(Home home) {
        String icon = home.getIcon();
        World.Environment environment = home.getWorldEnvironment();
        Material material = icon != null ? Material.getMaterial(icon) : null;
        if (material == null) {
            switch (environment) {
                case NETHER:
                    material = Material.NETHERRACK;
                    break;
                case THE_END:
                    material = Material.END_STONE;
                    break;
                default:
                    material = Material.GRASS_BLOCK;
            }
        }

        Timestamp ts = home.getSqlTimestamp();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());
        String year     = String.valueOf(cal.get(Calendar.YEAR));
        String month    = String.valueOf(cal.get(Calendar.MONTH));
        String day      = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String hour     = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String minute   = String.valueOf(cal.get(Calendar.MINUTE));
        String second   = String.valueOf(cal.get(Calendar.SECOND));

        String lore = Message.GUI_HOME_LORE.get();
        lore = lore.replace("%home_worldname%", home.getWorldName());
        lore = lore.replace("%year%", year);
        lore = lore.replace("%month%", month);
        lore = lore.replace("%day%", day);
        lore = lore.replace("%hour%", hour);
        lore = lore.replace("%minute%", minute);
        lore = lore.replace("%second%", second);

        ItemBuilder builder = ItemBuilder.from(material);
        builder.setName(Message.GUI_HOME_NAME.replace("%home_name%", home.getName()));
        builder.setLore(lore.split("\n"));

        return builder.build();
    }

}
