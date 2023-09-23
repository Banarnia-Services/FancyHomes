package de.banarnia.fancyhomes.api;

import com.google.common.collect.Lists;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Utility class for easy item creation.
 */
public final class ItemBuilder {

    private ItemStack item;
    private ItemMeta meta;

    /**
     * Create ItemBuilder by String. Use "Skull:" or "URL:" for skulls.
     * @param s Input String.
     * @return New instance.
     */
    public static ItemBuilder of(String s) {
        return new ItemBuilder(s);
    }

    /**
     * Create ItemBuilder by String. Use "Skull:" or "URL:" for skulls.
     * @param s Input String.
     * @param def Default material.
     * @return New instance.
     */
    public static ItemBuilder of(String s, Material def) {
        return new ItemBuilder(s, def);
    }

    /**
     * Create ItemBuilder by String. Use "Skull:" or "URL:" for skulls.
     * @param s Input String.
     * @param def Default item.
     * @return New instance.
     */
    public static ItemBuilder of(String s, ItemStack def) {
        return new ItemBuilder(s, def);
    }

    /**
     * Create ItemBuilder by Material.
     * @param material Material.
     * @return New instance.
     */
    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    /**
     * Create ItemBuilder by String. Use "Skull:" or "URL:" for skulls.
     * @param material Material.
     * @param flags ItemFlags to add.
     * @return New instance.
     */
    public static ItemBuilder of(Material material, ItemFlag... flags) {
        return new ItemBuilder(material, flags);
    }

    /**
     * Create ItemBuilder by String. Use "Skull:" or "URL:" for skulls.
     * @param item Existing item.
     * @return New instance.
     */
    public static ItemBuilder of(ItemStack item) {
        return new ItemBuilder(item);
    }

    /**
     * Create ItemBuilder by String. Use "Skull:" or "URL:" for skulls.
     * @param item Existing item.
     * @param flags ItemFlags to add.
     * @return New instance.
     */
    public static ItemBuilder of(ItemStack item, ItemFlag... flags) {
        return new ItemBuilder(item, flags);
    }

    /**
     * Constructor with String as argument.
     * @param s Material name, skull name or skull url.
     */
    private ItemBuilder(String s) {
        this(s, (Material) null);
    }

    /**
     * Constructor with String and default material as argument.
     * @param s Material name, skull name or skull url.
     * @param def Default Material.
     */
    private ItemBuilder(String s, Material def) {
        this(s, def != null ? new ItemStack(def) : null);
    }

    /**
     * Constructor with String and default ItemStack as argument.
     * @param s Material name, skull name or skull url.
     * @param def Default item if param s is not valid.
     */
    private ItemBuilder(String s, ItemStack def) {
        // Null check.
        if ((s == null || s.length() == 0) && def == null)
            throw new IllegalArgumentException();

        if (s != null && s.length() != 0) {
            ItemStack item = null;

            // Check if String is a material.
            Material material = Material.getMaterial(s);
            if (material != null) {
                // Create item stack.
                item = new ItemStack(material);

                // Init.
                init(item, null);

                return;
            }
        }

        // Check if default material exists.
        if (def != null) {
            init(new ItemStack(def));

            return;
        }

        throw new IllegalArgumentException("Could not create ItemStack with input: " + s);
    }

    /**
     * Constructor with material.
     * @param material Material.
     */
    private ItemBuilder(Material material) {
        this(material, null);
    }

    /**
     * Constructor with material and flags.
     * @param material Material.
     * @param flags ItemFlag array.
     */
    private ItemBuilder(Material material, ItemFlag... flags) {
        this(new ItemStack(material), flags);
    }

    /**
     * Constructor with ItemStack.
     * @param item ItemStack.
     */
    private ItemBuilder(ItemStack item) {
        this(item, null);
    }

    /**
     * Constructor with ItemStack and ItemFlags.
     * @param item ItemStack.
     * @param flags ItemFlag array.
     */
    private ItemBuilder(ItemStack item, ItemFlag... flags) {
        // Init.
        init(item, flags);
    }

    /**
     * Init method to add metadata after construction.
     * @param item ItemStack.
     * @param flags ItemFlag array.
     */
    private void init(ItemStack item, ItemFlag... flags) {
        // Null check.
        if (item == null)
            throw new IllegalArgumentException();

        // Set instance.
        this.item = item;

        // Check if item has item meta.
        if (item.getItemMeta() != null)
            this.meta           = item.getItemMeta();

        // Set item flags.
        addItemFlag(flags);
    }

    /**
     * Create the ItemStack.
     * @return Built ItemStack.
     */
    public ItemStack build() {
        // Set meta data.
        this.item.setItemMeta(meta);

        return item;
    }

    /**
     * Change items display name.
     * @param name Display name.
     * @return Instance for chaining.
     */
    public ItemBuilder name(String name) {
        // Null check.
        if (name == null)
            throw new IllegalArgumentException();

        // Check if meta exists.
        if (meta == null) {
            warnMetaNotExisting();

            return this;
        }
        meta.setDisplayName(name);

        return this;
    }

    /**
     * Change display name color.
     * @param color New color.
     * @return Instance for chaining.
     */
    public ItemBuilder colorName(ChatColor color) {
        // Null check.
        if (color == null)
            throw new IllegalArgumentException();

        // Get current display name.
        String displayName      = getDisplayName();
        String newDisplayName   = color + displayName;

        // Set name.
        return this.name(newDisplayName);
    }

    /**
     * Get display name of the item.
     * @return Instance for chaining.
     */
    public String getDisplayName() {
        // Check for item meta.
        if (meta != null && meta.hasDisplayName())
            return meta.getDisplayName();

        // Return translation if meta does not exist.
        return item.getType().toString();
    }

    /**
     * Reset display name.
     * @return Instance for chaining.
     */
    public ItemBuilder resetName() {
        // Check if meta exists.
        if (meta == null) {
            warnMetaNotExisting();

            return this;
        }

        // Reset name.
        meta.setDisplayName(null);

        return this;
    }

    /**
     * Get item lore.
     * @return Item lore.
     */
    public List<String> getLore() {
        return meta != null && meta.hasLore() ? meta.getLore() : Lists.newArrayList();
    }

    /**
     * Add lines to the lore.
     * @param lore Lines to add, or null for blank line.
     * @return Instance for chaining.
     */
    public ItemBuilder lore(List<String> lore) {
        // Null check.
        if (lore == null || lore.isEmpty())
            this.lore(" ");

        // Transform list to array.
        String[] loreArray = lore.toArray(new String[lore.size()]);

        return this.lore(loreArray);
    }

    /**
     * Add lines to the lore.
     * @param lore Lines to add, or null for blank line.
     * @return Instance for chaining.
     */
    public ItemBuilder lore(String... lore) {
        // Check current lore.
        List<String> newLore = getLore();

        // Add lines to lore.
        for (String line : lore)
            newLore.add(line == null ? " " : ChatColor.GRAY + line);

        // Set new lore.
        meta.setLore(newLore);

        return this;
    }

    public ItemBuilder lore(Location location) {
        String cross = "§4✖";
        String world = "World: §e" + (location != null && location.getWorld() != null ? location.getWorld().getName() : cross);
        String x = "X: §e" + (location != null ? String.valueOf(UtilMath.unsafeRound(location.getX(), 2)) : cross);
        String y = "Y: §e" + (location != null ? String.valueOf(UtilMath.unsafeRound(location.getY(), 2)) : cross);
        String z = "Z: §e" + (location != null ? String.valueOf(UtilMath.unsafeRound(location.getZ(), 2)) : cross);
        String yaw = "Yaw: §e" + (location != null ? String.valueOf(UtilMath.unsafeRound(location.getYaw(), 2)) : cross);
        String pitch = "Pitch: §e" + (location != null ? String.valueOf(UtilMath.unsafeRound(location.getPitch(), 2)) : cross);

        lore(world);
        lore(x);
        lore(y);
        lore(z);

        if (location != null && location.getYaw() != 0 && location.getPitch() != 0) {
            lore(yaw);
            lore(pitch);
        }

        return this;
    }

    /**
     * Clear lore.
     * @return Instance for chaining.
     */
    public ItemBuilder clearLore() {
        // Check if meta exists.
        if (meta == null) {
            warnMetaNotExisting();

            return this;
        }

        // Set lore with empty list.
        meta.setLore(Lists.newArrayList());

        return this;
    }

    /**
     * Remove enchantments.
     * @return Instance for chaining.
     */
    public ItemBuilder clearEnchantments() {
        // Check if item meta exists.
        if (meta == null) {
            warnMetaNotExisting();

            return this;
        }

        // Remove enchantments.
        for (Enchantment enchantment : meta.getEnchants().keySet())
            meta.removeEnchant(enchantment);

        return this;
    }

    /**
     * Add an enchantment with level 1 and level restriction..
     * @param enchantment Enchantment to add.
     * @return Instance for chaining.
     */
    public ItemBuilder addEnchantment(Enchantment enchantment) {
        return addEnchantment(enchantment, 1, false);
    }

    /**
     * Add an enchantment.
     * @param enchantment Enchantment to add.
     * @param level Enchantment level.
     * @param ignoreLevelRestriction Ignore the level restriction.
     * @return Instance for chaining.
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        // Check if meta exists.
        if (meta == null) {
            warnMetaNotExisting();

            return this;
        }

        // Add enchantment.
        meta.addEnchant(enchantment, level, ignoreLevelRestriction);

        return this;
    }

    /**
     * Add an ItemFlag.
     * @param flags ItemFlags to add.
     * @return Instance for chaining.
     */
    public ItemBuilder addItemFlag(ItemFlag... flags) {
        // Null check.
        if (flags == null)
            return this;

        // Check length.
        if (flags.length == 0)
            return this;

        // Add flags.
        for (ItemFlag flag : flags) {
            // Null check.
            if (flag == null) {
                Bukkit.getLogger().warning("Failed to apply an ItemFlag: Null.");
                continue;
            }

            // Add flag.
            meta.addItemFlags(flag);
        }

        return this;
    }

    /**
     * Remove ItemFlag.
     * @param flags ItemFlags to remove.
     * @return Instance for chaining.
     */
    public ItemBuilder removeItemFlag(ItemFlag... flags) {
        // Null check.
        if (flags == null)
            throw new IllegalArgumentException();

        // Check for item meta.
        if (meta == null) {
            Bukkit.getLogger().warning("Failed to ");
            return this;
        }

        // Check length.
        if (flags.length == 0)
            return this;

        // Remove flags.
        for (ItemFlag flag : flags) {
            // Null check.
            if (flag == null) {
                Bukkit.getLogger().warning("Failed to apply an ItemFlag: Null.");
                continue;
            }

            // Remove flag.
            meta.removeItemFlags(flag);
        }

        return this;
    }

    /**
     * Hide item enchantments.
     * @return Instance for chaining.
     */
    public ItemBuilder hideEnchants() {
        // Flag setzen
        return addItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    /**
     * Change skull to player skin.
     * @param player Player whose head you want.
     * @return Instance for chaining.
     */
    public ItemBuilder setSkullOwner(@Nullable OfflinePlayer player) {
        // Check if item is a skull.
        if (item.getType() != Material.PLAYER_HEAD) {
            Bukkit.getLogger().warning("You can only change the Skull-Owner of skulls.");
            return this;
        }

        // Get SkullMeta.
        SkullMeta skullMeta = (SkullMeta) this.meta;

        // Change owner.
        skullMeta.setOwningPlayer(player);

        return this;
    }

    /**
     * Warn if ItemMeta does not exist.
     */
    private void warnMetaNotExisting() {
        Bukkit.getLogger().warning("Failed to modify ItemMeta - ItemMeta is null");
    }

}