package de.banarnia.fancyhomes.gui;

import de.banarnia.fancyhomes.api.UtilGUI;
import de.banarnia.fancyhomes.lang.Message;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.util.Legacy;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.function.Consumer;

public class MaterialSelectionGUI {

    private String title;
    private Material originSelection;
    private Material currentSelection;
    private String acceptItemName;
    private List<String> acceptItemLore;
    private String cancelItemName;
    private List<String> cancelItemLore;
    private Sound acceptSound;
    private Sound cancelSound;
    private Consumer<Material> consumer;

    private PaginatedGui gui;

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
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.BLOCK_NOTE_BLOCK_BANJO,
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
            gui.addItem(getMaterialItem(material));
        }

        UtilGUI.setPaginationItems(gui, Message.GUI_HOME_PAGE_PREVIOUS.get(), Message.GUI_HOME_PAGE_NEXT.get());
    }

    private GuiItem getMaterialItem(Material material) {
        if (material == null)
            return null;

        ItemBuilder builder = ItemBuilder.from(material);
        if (currentSelection == material) {
            builder.enchant(Enchantment.INFINITY);
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
            player.playSound(player, acceptSound, 1, 1);

        consumer.accept(currentSelection);
    }

    private void deny(Player player) {
        if (this.consumer == null)
            return;

        Consumer<Material> consumer = this.consumer;
        this.consumer = null;

        if (this.cancelSound != null)
            player.playSound(player, cancelSound, 1, 1);

        consumer.accept(originSelection);
    }

}
