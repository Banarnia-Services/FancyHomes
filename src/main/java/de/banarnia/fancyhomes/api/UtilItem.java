package de.banarnia.fancyhomes.api;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class UtilItem {

    public static ItemStack getPlayerSkull(OfflinePlayer offlinePlayer) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(offlinePlayer.getName());
        skull.setOwningPlayer(offlinePlayer);
        item.setItemMeta(skull);
        return item;
    }

}
