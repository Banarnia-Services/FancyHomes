package de.banarnia.fancyhomes.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import de.banarnia.fancyhomes.api.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ConfirmationGUI extends ChestGui {

    public static void open(Player player, String title, Consumer<Boolean> acceptOrDeny) {
        new ConfirmationGUI(title, player, acceptOrDeny).show(player);
    }

    private Consumer<Boolean> acceptOrDeny;

    public ConfirmationGUI(String title, Player player, Consumer<Boolean> acceptOrDeny) {
        super(3, title);
        this.acceptOrDeny = acceptOrDeny;

        setOnGlobalClick(event -> event.setCancelled(true));
        setOnClose(event -> deny(player));

        OutlinePane background = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));
        background.setRepeat(true);
        addPane(background);

        OutlinePane accept = new OutlinePane(2, 1, 1, 1);
        ItemBuilder builder = ItemBuilder.of(Material.EMERALD_BLOCK).name("§2✔");
        accept.addItem(new GuiItem(builder.build(), click -> accept(player)));
        addPane(accept);

        OutlinePane deny = new OutlinePane(6, 1, 1, 1);
        builder = ItemBuilder.of(Material.REDSTONE_BLOCK).name("§4✖");
        deny.addItem(new GuiItem(builder.build(), click -> {
            deny(player);
        }));
        addPane(deny);
    }

    private void accept(Player player) {
        if (this.acceptOrDeny == null)
            return;

        Consumer<Boolean> consumer = this.acceptOrDeny;
        this.acceptOrDeny = null;
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        consumer.accept(true);
    }

    private void deny(Player player) {
        if (this.acceptOrDeny == null)
            return;

        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 1);
        this.acceptOrDeny.accept(false);
        this.acceptOrDeny = null;
    }

}
