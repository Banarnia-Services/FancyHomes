package de.banarnia.fancyhomes.api;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class UtilGUI {

    public static void setPaginationItems(PaginatedGui gui, int prevRow, int prevCol, int nextRow, int nextCol,
                                          ItemStack prevItem, ItemStack nextItem) {
        gui.setItem(prevRow, prevCol, ItemBuilder.from(prevItem).asGuiItem(event -> {
            event.setCancelled(true);
            gui.previous();
        }));

        gui.setItem(nextRow, nextCol, ItemBuilder.from(nextItem).asGuiItem(event -> {
            event.setCancelled(true);
            gui.next();
        }));
    }

    public static void setPaginationItems(PaginatedGui gui, int row, ItemStack prevItem, ItemStack nextItem) {
        setPaginationItems(gui, row, 2, row, 8, prevItem, nextItem);
    }

    public static void setPaginationItems(PaginatedGui gui, int row, Material material, String prevName, String nextName) {
        ItemStack prevItem = ItemBuilder.from(material).setName(prevName).build();
        ItemStack nextItem = ItemBuilder.from(material).setName(nextName).build();
        setPaginationItems(gui, row, prevItem, nextItem);
    }

    public static void setPaginationItems(PaginatedGui gui, int row, Material material) {
        setPaginationItems(gui, row, material, "§cPrevious", "§aNext");
    }

    public static void setPaginationItems(PaginatedGui gui, String prevName, String nextName) {
        setPaginationItems(gui, gui.getRows(), Material.ARROW, prevName, nextName);
    }

    public static void setPaginationItems(PaginatedGui gui, int row) {
        setPaginationItems(gui, row, Material.ARROW);
    }

    public static void setPaginationItems(PaginatedGui gui) {
        setPaginationItems(gui, gui.getRows(), Material.ARROW);
    }

}
