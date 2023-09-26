package de.banarnia.fancyhomes.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.components.util.Legacy;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public class ConfirmationGUI {

    private String title;
    private String acceptItemName;
    private List<String> acceptItemLore;
    private String denyItemName;
    private List<String> denyItemLore;
    private Sound acceptSound;
    private Sound denySound;
    private Consumer<Boolean> consumer;

    private Gui gui;

    public ConfirmationGUI(String title, String acceptItemName, List<String> acceptItemLore,
                           String denyItemName, List<String> denyItemLore,
                           Sound acceptSound, Sound denySound, Consumer<Boolean> consumer) {
        this.title = title;
        this.acceptItemName = acceptItemName != null ? acceptItemName : "§2✔";
        this.acceptItemLore = acceptItemLore;
        this.denyItemName = denyItemName != null ? denyItemName : "§4✖";
        this.denyItemLore = denyItemLore;
        this.acceptSound = acceptSound != null ? acceptSound : Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        this.denySound = denySound != null ? denySound : Sound.BLOCK_NOTE_BLOCK_BANJO;
        this.consumer = consumer;

        this.gui = Gui.gui(GuiType.HOPPER).title(Legacy.SERIALIZER.deserialize(title)).create();
        this.gui.setDefaultClickAction(event -> event.setCancelled(true));
        this.gui.setCloseGuiAction(event -> deny((Player) event.getPlayer()));
        init();
    }

    public ConfirmationGUI(String title, Consumer<Boolean> consumer) {
        this(title, null, null, null, null, null, null, consumer);
    }

    public ConfirmationGUI(String title, String acceptItemName, String denyItemName, Consumer<Boolean> consumer) {
        this(title, acceptItemName, null, denyItemName, null, null, null, consumer);
    }

    public void init() {
        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" ").asGuiItem());

        GuiItem acceptItem = ItemBuilder.from(Material.EMERALD_BLOCK)
                .setName(acceptItemName)
                .setLore(acceptItemLore)
                .asGuiItem();
        acceptItem.setAction(click -> accept((Player) click.getWhoClicked()));
        gui.setItem(2, acceptItem);

        GuiItem denyItem = ItemBuilder.from(Material.REDSTONE_BLOCK)
                .setName(denyItemName)
                .setLore(denyItemLore)
                .asGuiItem();
        denyItem.setAction(click -> deny((Player) click.getWhoClicked()));
        gui.setItem(6, denyItem);
    }

    public void open(Player player) {
        this.gui.open(player);
    }

    private void accept(Player player) {
        if (this.consumer == null)
            return;

        Consumer<Boolean> consumer = this.consumer;
        this.consumer = null;
        player.playSound(player, acceptSound, 1, 1);
        consumer.accept(true);
    }

    private void deny(Player player) {
        if (this.consumer == null)
            return;

        player.playSound(player, denySound, 1, 1);
        this.consumer.accept(false);
        this.consumer = null;
    }

}
