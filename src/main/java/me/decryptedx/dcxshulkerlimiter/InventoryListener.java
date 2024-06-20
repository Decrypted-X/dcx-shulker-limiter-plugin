package me.decryptedx.dcxshulkerlimiter;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.logging.Level;

/// Listen to various inventory events.
public class InventoryListener implements Listener {
    /// The number of shulker boxes the player can have given by plugin configs.
    private final int CONF_SHULKER_PLAYER_LIMIT;

    /// The number of shulker boxes a player's ender chest can have given by plugin configs.
    private final int CONF_SHULKER_ENDER_CHEST_LIMIT;

    /// The number of shulker boxes other inventories can have given by plugin configs.
    private final int CONF_SHULKER_OTHER_LIMIT;

    private final boolean CONF_ADDITIVE;

    /// A list of all shulker box material types.
    private static final Material[] SHULKER_BOXES = {
            Material.SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.BLACK_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX
    };

    // Permission nodes, append with integer on end to determine limit
    private final static String PLAYER_PERM_PREFIX = "dcxshulkerlimiter.player.limit.";
    private final static String ENDER_CHEST_PERM_PREFIX = "dcxshulkerlimiter.echest.limit.";

    public InventoryListener(FileConfiguration config) {
        CONF_SHULKER_PLAYER_LIMIT = config.getInt("PlayerShulkerLimit");
        CONF_SHULKER_ENDER_CHEST_LIMIT = config.getInt("EnderChestShulkerLimit");
        CONF_SHULKER_OTHER_LIMIT = config.getInt("OtherShulkerLimit");
        CONF_ADDITIVE = config.getBoolean("AdditivePermission");
    }

    /**Check whether the material is a type of shulker box.
     *
     * @param material The material to check if it is a shulker box.
     * @return A boolean that indicates whether the material is a type of shulker box.
     */
    private boolean isShulkerBox(Material material) {
        for (Material shulkerBoxType : SHULKER_BOXES)
            if (material == shulkerBoxType)
                return true;

        return false;
    }

    /**Check whether the inventory given contains a type of shulker box.
     * <p>
     * An amount of 0 will always be true (there will always be at least 0 shulker boxes). A negative amount will return
     * false (there can never be negative shulker boxes).
     *
     * @param inventory The inventory to check if it contains a type of shulker box.
     * @param amount The amount of shulker boxes to check for in the inventory.
     * @return A boolean that indicates whether the material is a type of shulker box.
     */
    private boolean containsShulkerBox(Inventory inventory, int amount) {
        if (amount == 0)
            return true;
        else if (inventory == null || amount < 0)
            return false;

        int total = 0;

        // get the total number of shulker boxes from the inventory until the amount specified is reached
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && isShulkerBox(itemStack.getType())) {
                total++;

                if (total >= amount)
                    return true;
            }
        }

        return false;
    }

    /**Check whether the inventory has the max allowed shulker boxes.
     *
     * @param inventory The inventory to check if it has the max allowed shulker boxes given its type.
     * @return A boolean that indicates whether the inventory contains the max allowed shulker boxes.
     */
    private boolean checkMaxShulker(Inventory inventory, Player player) {
        if (inventory == null)
            return false;

        return switch (inventory.getType()) {
            case PLAYER -> containsShulkerBox(
                    inventory,
                    getShulkerLimit(player, PLAYER_PERM_PREFIX, CONF_SHULKER_PLAYER_LIMIT, CONF_ADDITIVE)
            );
            case ENDER_CHEST -> containsShulkerBox(
                    inventory,
                    getShulkerLimit(player, ENDER_CHEST_PERM_PREFIX, CONF_SHULKER_ENDER_CHEST_LIMIT, CONF_ADDITIVE)
            );
            default -> containsShulkerBox(
                    inventory,
                    CONF_SHULKER_OTHER_LIMIT
            );
        };
    }

    public int getShulkerLimit(Player player, String prefix, int defaultValue, boolean additive) {
        if (player == null) return defaultValue;

        final LuckPerms permissionProvider = DCXShulkerLimiterPlugin.getLuckPerms();
        final int amount;

        final List<Node> permissions = permissionProvider.getPlayerAdapter(Player.class)
                                                         .getUser(player)
                                                         .resolveInheritedNodes(QueryOptions.nonContextual())
                                                         .stream().toList();
        try {
            if (additive) {
                amount = permissions
                        .stream()
                        .filter(Node::getValue) // only true nodes
                        .filter(node -> !node.hasExpired())
                        .map(Node::getKey)
                        .filter(permission -> permission.startsWith(prefix))
                        .mapToInt(InventoryListener::integerValueFromPermission)
                        .sum();
            }
            else {
                amount = permissions
                        .stream()
                        .filter(Node::getValue) // only true nodes
                        .filter(node -> !node.hasExpired())
                        .map(Node::getKey)
                        .filter(permission -> permission.startsWith(prefix))
                        .mapToInt(InventoryListener::integerValueFromPermission)
                        .max()
                        .orElse(defaultValue);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Exception while getting shulker limit for " + player.getName() + ", returning default.", e);
            return defaultValue;
        }

        return amount;
    }

    /**Get the integer value of a permission.
     *
     * @param permission The permission to get the integer value from.
     * @return An integer that is the value retrieved from the permission.
     * @throws NumberFormatException Thrown if permission contains invalid value.
     * @throws IndexOutOfBoundsException Thrown if permission is in invalid format.
     */
    public static int integerValueFromPermission(String permission) throws NumberFormatException, IndexOutOfBoundsException {
        return Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        Player player = null;

        if (event.getWhoClicked() instanceof Player)
            player = (Player) event.getWhoClicked();

        // check clicked inventory for shulker boxes for any place actions
        if ((action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE ||
                action == InventoryAction.PLACE_SOME) && event.getCursor() != null &&
                isShulkerBox(event.getCursor().getType()) && checkMaxShulker(event.getClickedInventory(), player)) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(ChatColor.RED + "Unable to move shulker box!");
        }
        // check clicked inventory and cursor for shulker boxes for swap action
        else if (action == InventoryAction.SWAP_WITH_CURSOR) {
            ItemStack heldItem = event.getCursor();
            ItemStack otherItem = event.getCurrentItem();

            // swapping a shulker box with a shulker box does not need to be checked
            if (heldItem != null && otherItem != null && isShulkerBox(heldItem.getType()) &&
                    isShulkerBox(otherItem.getType()))
                return;

            // check clicked inventory for shulker box if cursor is shulker box
            if (heldItem != null && isShulkerBox(heldItem.getType()) &&
                    checkMaxShulker(event.getClickedInventory(), player)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(ChatColor.RED + "Unable to move shulker box!");
            }
        }
        // check other inventory for shulker boxes when moving
        else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY && event.getCurrentItem() != null &&
                isShulkerBox(event.getCurrentItem().getType())) {
            Inventory clickedInventory = event.getClickedInventory();
            Inventory inventory = event.getInventory();
            Inventory playerInventory = event.getWhoClicked().getInventory();
            Inventory otherInventory = (clickedInventory == inventory) ? playerInventory : inventory;

            if (checkMaxShulker(otherInventory, player)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(ChatColor.RED + "Unable to move shulker box!");
            }
        }
        // check both inventories for shulker boxes when using hotbar actions
        else if (action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD) {
            Inventory clickedInventory = event.getClickedInventory();
            Inventory playerInventory = event.getWhoClicked().getInventory();
            int hotbarButton = event.getHotbarButton();

            if (clickedInventory != playerInventory && hotbarButton != -1) {
                ItemStack clickedItem = event.getCurrentItem();
                ItemStack hotbarItem = playerInventory.getItem(hotbarButton);

                // swapping a shulker box with a shulker box does not need to be checked
                if (clickedItem != null && hotbarItem != null && isShulkerBox(clickedItem.getType()) &&
                        isShulkerBox(hotbarItem.getType()))
                    return;

                // check player inventory for shulker box if clicked item is shulker box
                if (clickedItem != null && isShulkerBox(clickedItem.getType()) &&
                        checkMaxShulker(playerInventory, player)) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "Unable to move shulker box!");
                }

                // check clicked inventory for shulker box if player hotbar item is shulker box
                if (!event.isCancelled() && hotbarItem != null && isShulkerBox(hotbarItem.getType()) &&
                        checkMaxShulker(clickedInventory, player)) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "Unable to move shulker box!");
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryDragEvent(InventoryDragEvent event) {
        // cancel dragging any additional shulker boxes into an inventory
        if (isShulkerBox(event.getOldCursor().getType()) && event.getWhoClicked() instanceof Player player) {
            for (int slot : event.getRawSlots()) {
                if (checkMaxShulker(event.getView().getInventory(slot), player)) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "Unable to move shulker box!");
                    break;
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        // cancel any additional shulker boxes from being picked up by a player
        if (isShulkerBox(event.getItem().getItemStack().getType()) && event.getEntity() instanceof Player player &&
                checkMaxShulker(player.getInventory(), player))
            event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
        // cancel any additional shulker boxes from being moved
        if (isShulkerBox(event.getItem().getType()) && checkMaxShulker(event.getDestination(), null))
            event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryPickupItemEvent(InventoryPickupItemEvent event) {
        // cancel any additional shulker boxes from being picked up
        if (isShulkerBox(event.getItem().getItemStack().getType()) && checkMaxShulker(event.getInventory(), null))
            event.setCancelled(true);
    }
}
