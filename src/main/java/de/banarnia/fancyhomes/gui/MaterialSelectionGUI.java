package de.banarnia.fancyhomes.gui;

import de.banarnia.api.UtilGUI;
import de.banarnia.api.triumphgui.builder.item.ItemBuilder;
import de.banarnia.api.triumphgui.components.util.Legacy;
import de.banarnia.api.triumphgui.guis.Gui;
import de.banarnia.api.triumphgui.guis.GuiItem;
import de.banarnia.api.triumphgui.guis.PaginatedGui;
import de.banarnia.fancyhomes.lang.Message;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.function.Consumer;

public class MaterialSelectionGUI {

    private final String title;
    private final Material originSelection;
    private Material currentSelection;
    private final String acceptItemName;
    private final List<String> acceptItemLore;
    private final String cancelItemName;
    private final List<String> cancelItemLore;
    private final Sound acceptSound;
    private final Sound cancelSound;
    private Consumer<Material> consumer;

    private final PaginatedGui gui;

    public MaterialSelectionGUI(String title, Material originSelection,
                                String acceptItemName, List<String> acceptItemLore,
                                String cancelItemName, List<String> cancelItemLore,
                                Sound acceptSound, Sound cancelSound, Consumer<Material> consumer) {
        this.title = title;
        this.originSelection = originSelection;
        this.currentSelection = originSelection;
        this.acceptItemName = acceptItemName;
        this.acceptItemLore = acceptItemLore;
        this.cancelItemName = cancelItemName;
        this.cancelItemLore = cancelItemLore;
        this.acceptSound = acceptSound;
        this.cancelSound = cancelSound;
        this.consumer = consumer;

        this.gui = Gui.paginated().title(Legacy.SERIALIZER.deserialize(title)).rows(6).create();
        this.gui.setDefaultClickAction(event -> event.setCancelled(true));
        this.gui.setCloseGuiAction(event -> deny((Player) event.getPlayer()));
        init();
    }

    public MaterialSelectionGUI(String title, Material originSelection, String acceptItemName, String cancelItemName, Consumer<Material> consumer) {
        this(title, originSelection,
                acceptItemName, null, cancelItemName, null,
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.BLOCK_NOTE_BLOCK_GUITAR,
                consumer);
    }

    private void init() {
        gui.clearPageItems();

        gui.getFiller().fillBottom(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" ").asGuiItem());

        GuiItem acceptItem = ItemBuilder.from(Material.EMERALD)
                .setName(acceptItemName)
                .setLore(acceptItemLore)
                .asGuiItem();
        acceptItem.setAction(click -> accept((Player) click.getWhoClicked()));
        gui.setItem(gui.getRows(), 4, acceptItem);

        GuiItem denyItem = ItemBuilder.from(Material.BARRIER)
                .setName(cancelItemName)
                .setLore(cancelItemLore)
                .asGuiItem();
        denyItem.setAction(click -> deny((Player) click.getWhoClicked()));
        gui.setItem(gui.getRows(), 6, denyItem);

        for (Material material : Material.values()) {
            if (material == Material.AIR) continue;
            GuiItem guiItem = getMaterialItem(material);
            if (guiItem != null)
                gui.addItem(getMaterialItem(material));
        }

        UtilGUI.setPaginationItems(gui, Message.GUI_HOME_PAGE_PREVIOUS.get(), Message.GUI_HOME_PAGE_NEXT.get());
    }

    private GuiItem getMaterialItem(Material material) {
        if (material == null)
            return null;

        ItemBuilder builder;

        // Fix for 1.21 update, where Materials like WATER cannot be converted to an ItemStack.
        try {
            builder = ItemBuilder.from(material);
        } catch (IllegalArgumentException ex) {
            return null;
        }

        if (currentSelection == material) {
            builder.enchant(Enchantment.FIRE_ASPECT);
            builder.flags(ItemFlag.HIDE_ENCHANTS);
        }

        return builder.asGuiItem(click -> {
            this.currentSelection = material;
            init();
            gui.update();
        });
    }

    public void open(Player player) {
        this.gui.open(player);
    }

    private void accept(Player player) {
        if (this.consumer == null)
            return;

        Consumer<Material> consumer = this.consumer;
        this.consumer = null;

        if (this.acceptSound != null)
            player.playSound(player.getLocation(), acceptSound, 1, 1);

        consumer.accept(currentSelection);
    }

    private void deny(Player player) {
        if (this.consumer == null)
            return;

        Consumer<Material> consumer = this.consumer;
        this.consumer = null;

        if (this.cancelSound != null)
            player.playSound(player.getLocation(), cancelSound, 1, 1);

        consumer.accept(originSelection);
    }

}
