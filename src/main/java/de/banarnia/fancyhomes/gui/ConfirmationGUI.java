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

    private final String title;
    private final String acceptItemName;
    private final List<String> acceptItemLore;
    private final String denyItemName;
    private final List<String> denyItemLore;
    private final Sound acceptSound;
    private final Sound denySound;
    private Consumer<Boolean> consumer;

    private final Gui gui;

    public ConfirmationGUI(String title, String acceptItemName, List<String> acceptItemLore,
                           String denyItemName, List<String> denyItemLore,
                           Sound acceptSound, Sound denySound, Consumer<Boolean> consumer) {
        this.title = title;
        this.acceptItemName = acceptItemName != null ? acceptItemName : "§2✔";
        this.acceptItemLore = acceptItemLore;
        this.denyItemName = denyItemName != null ? denyItemName : "§4✖";
        this.denyItemLore = denyItemLore;
        this.acceptSound = acceptSound;
        this.denySound = denySound;
        this.consumer = consumer;

        this.gui = Gui.gui(GuiType.CHEST).title(Legacy.SERIALIZER.deserialize(title)).create();
        this.gui.setDefaultClickAction(event -> event.setCancelled(true));
        this.gui.setCloseGuiAction(event -> deny((Player) event.getPlayer()));
        init();
    }

    public ConfirmationGUI(String title, Consumer<Boolean> consumer) {
        this(title, null, null, null, null, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.BLOCK_NOTE_BLOCK_GUITAR, consumer);
    }

    public ConfirmationGUI(String title, String acceptItemName, String denyItemName, Consumer<Boolean> consumer) {
        this(title, acceptItemName, null, denyItemName, null, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.BLOCK_NOTE_BLOCK_GUITAR, consumer);
    }

    public void init() {
        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" ").asGuiItem());

        GuiItem acceptItem = ItemBuilder.from(Material.EMERALD_BLOCK)
                .setName(acceptItemName)
                .setLore(acceptItemLore)
                .asGuiItem();
        acceptItem.setAction(click -> accept((Player) click.getWhoClicked()));
        gui.setItem(3, acceptItem);

        GuiItem denyItem = ItemBuilder.from(Material.REDSTONE_BLOCK)
                .setName(denyItemName)
                .setLore(denyItemLore)
                .asGuiItem();
        denyItem.setAction(click -> deny((Player) click.getWhoClicked()));
        gui.setItem(5, denyItem);
    }

    public void open(Player player) {
        this.gui.open(player);
    }

    private void accept(Player player) {
        if (this.consumer == null)
            return;

        Consumer<Boolean> consumer = this.consumer;
        this.consumer = null;

        if (this.acceptSound != null)
            player.playSound(player.getLocation(), acceptSound, 1, 1);

        consumer.accept(true);
    }

    private void deny(Player player) {
        if (this.consumer == null)
            return;

        Consumer<Boolean> consumer = this.consumer;
        this.consumer = null;

        if (this.denySound != null)
            player.playSound(player.getLocation(), denySound, 1, 1);

        consumer.accept(false);
    }

}
