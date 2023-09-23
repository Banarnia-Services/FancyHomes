package de.banarnia.fancyhomes.gui;

import com.github.stefvanschie.inventoryframework.font.util.Font;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Orientable;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.component.Label;
import de.banarnia.fancyhomes.FancyHomes;
import de.banarnia.fancyhomes.FancyHomesAPI;
import de.banarnia.fancyhomes.api.ItemBuilder;
import de.banarnia.fancyhomes.api.UtilItem;
import de.banarnia.fancyhomes.api.UtilThread;
import de.banarnia.fancyhomes.data.HomeData;
import de.banarnia.fancyhomes.data.storage.Home;
import de.banarnia.fancyhomes.lang.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.BiPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;

public class HomeGUI extends ChestGui {

    public static boolean open(Player player, OfflinePlayer target, HomeData data) {
        if (player == null || target == null)
            return false;

        new HomeGUI(3, "§e" + target.getName() + "§'s Homes", player, target, data).show(player);
        return true;
    }

    public HomeGUI(int rows, String title, Player player, OfflinePlayer target, HomeData data) {
        super(rows, title);

        setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));
        background.setRepeat(true);
        addPane(background);

        OutlinePane infoPane = new OutlinePane(4, 0, 1, 1);
        infoPane.addItem(getInfoItem(data, target));
        addPane(infoPane);

        PaginatedPane homePaginatedPane = new PaginatedPane(1, 1, 7, 1);
        int homesPerPage = 7;
        Deque<String> homeNamesSorted = data.getHomeMap().keySet().stream().sorted().collect(Collectors.toCollection(LinkedList::new));
        for (int i = 0, pagesAmount = (homeNamesSorted.size() / homesPerPage) + 1; i < pagesAmount; i++) {
            OutlinePane homePane = new OutlinePane(0, 0, homesPerPage, 1);
            for (int j = 0; j < homesPerPage && !homeNamesSorted.isEmpty(); j++) {
                Home home = data.getHome(homeNamesSorted.removeFirst());
                homePane.addItem(getHomeItem(player, target, data, home).getItems().get(0));
            }
            homePaginatedPane.addPane(i, homePane);
        }
        addPane(homePaginatedPane);

        OutlinePane controlPane = new OutlinePane(0, 1, 9, 1, Pane.Priority.LOW);
        controlPane.setOrientation(Orientable.Orientation.HORIZONTAL);
        controlPane.setGap(7);
        controlPane.addItem(PageController.PREVIOUS.toItemStack(this, Message.GUI_HOME_PAGE_PREVIOUS.get(), homePaginatedPane));
        controlPane.addItem(PageController.NEXT.toItemStack(this, Message.GUI_HOME_PAGE_NEXT.get(), homePaginatedPane));
        addPane(controlPane);

        update();
    }

    private GuiItem getInfoItem(HomeData data, OfflinePlayer target) {
        ItemBuilder builder = ItemBuilder.of(UtilItem.getPlayerSkull(target));
        builder.name("§6" + target.getName());

        int limit = FancyHomesAPI.get().getHomeLimit(target.getUniqueId());
        builder.lore("§7Homes: §e" + data.getHomeMap().size() + (limit >= 0 ? "§7/§a" + limit : ""));

        return new GuiItem(builder.build());
    }

    private Label getHomeItem(Player player, OfflinePlayer target, HomeData data, Home home) {
        Label label = new Label(1,1, Font.GRAY);
        label.setText(String.valueOf(home.getName().charAt(0)),
        (character, item) -> {
            ItemBuilder builder = ItemBuilder.of(item);
            builder.name("§a" + home.getName());
            builder.lore("§7" + home.getSqlTimestamp());
            builder.lore(" ");
            builder.lore(home.getLocation());
            builder.lore(" ");
            builder.lore(Message.GUI_HOME_LEFTCLICK_TELEPORT.get());
            builder.lore(Message.GUI_HOME_RIGHTCLICK_DELETE.get());
            GuiItem guiItem = new GuiItem(builder.build(), click -> {
                if (click.isLeftClick()) {
                    FancyHomesAPI.get().teleport(player, home);
                    return;
                } else {
                    ConfirmationGUI.open(player, Message.GUI_CONFIRMATION_TITLE.get(), delete -> {
                        if (delete) {
                            FancyHomesAPI.get().deleteHome(player, target.getUniqueId(), home.getName())
                                    .thenRun(() -> UtilThread.runSync(FancyHomes.getInstance(), () -> open(player, target, data)));
                        } else
                            open(player, target, data);
                    });
                    return;
                }
            });
            return guiItem;
        });

        return label;
    }

    private enum PageController
    {
        PREVIOUS("MHF_ArrowLeft", (page, itemsPane) -> page > 0, page -> --page),
        NEXT("MHF_ArrowRight", (page, itemsPane) -> page < (itemsPane.getPages()-1), page -> ++page);

        private final String skullName;
        private final BiPredicate<Integer, PaginatedPane> shouldContinue;
        private final IntUnaryOperator nextPageSupplier;

        PageController(String skullName, BiPredicate<Integer, PaginatedPane> shouldContinue, IntUnaryOperator nextPageSupplier)
        {
            this.skullName = skullName;
            this.shouldContinue = shouldContinue;
            this.nextPageSupplier = nextPageSupplier;
        }

        @SuppressWarnings("deprecation")
        public GuiItem toItemStack(ChestGui gui, String itemName, PaginatedPane itemsPane)
        {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setDisplayName(itemName);
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(this.skullName));
            item.setItemMeta(meta);

            return new GuiItem(item, event ->
            {
                int currentPage = itemsPane.getPage();

                if(!this.shouldContinue.test(currentPage, itemsPane))
                    return;

                itemsPane.setPage(this.nextPageSupplier.applyAsInt(currentPage));
                gui.update();
            });
        }
    }

}
