package me.decryptedx.dcxshulkerlimiter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

/// Listen to various inventory events.
public class InventoryListener implements Listener {
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

    /**Get the integer value of a permission.
     *
     * @param permission The permission to get the integer value from.
     * @return An integer that is the value retrieved from the permission.
     * @throws NumberFormatException Thrown if permission contains invalid value.
     * @throws IndexOutOfBoundsException Thrown if permission is in invalid format.
     */
    private int integerValueFromPermission(String permission) throws NumberFormatException, IndexOutOfBoundsException {
        return Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
    }

    /**Check whether the inventory has the max allowed shulker boxes.
     *
     * @param inventory The inventory to check if it has the max allowed shulker boxes given its type.
     * @return A boolean that indicates whether the inventory contains the max allowed shulker boxes.
     */
    private boolean checkMaxShulker(Inventory inventory, Player player) {
        if (inventory == null)
            return false;

        boolean hasShulker = false;
        String echestPermPrefix = "dcxshulkerlimiter.echest.limit.";
        String playerPermPrefix = "dcxshulkerlimiter.player.limit.";

        int enderChestLimit = -1;
        int playerLimit = -1;
        int otherLimit = -1;

        // get limits for shulker boxes from player permission nodes
        if (player != null) {
            for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
                String permission = attachmentInfo.getPermission();

                try {
                    if (permission.startsWith(echestPermPrefix))
                        enderChestLimit = integerValueFromPermission(permission);
                    else if (permission.startsWith(playerPermPrefix))
                        playerLimit = integerValueFromPermission(permission);
                }
                catch (NumberFormatException | IndexOutOfBoundsException ignored) {}
            }
        }

        // check whether the inventory of each type has the max shulker amount
        switch (inventory.getType()) {
            case ENDER_CHEST -> hasShulker = containsShulkerBox(inventory, enderChestLimit);
            case PLAYER -> hasShulker = containsShulkerBox(inventory, playerLimit);
            case BARREL, CHEST, DISPENSER, DROPPER, HOPPER -> hasShulker = containsShulkerBox(inventory, otherLimit);
        }

        return hasShulker;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        InventoryAction action = event.getAction();
        Player player = null;

        if (event.getWhoClicked() instanceof Player)
            player = (Player) event.getWhoClicked();

        // check clicked inventory for shulker boxes for any place actions
        if ((action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE ||
                action == InventoryAction.PLACE_SOME || action == InventoryAction.SWAP_WITH_CURSOR) &&
                event.getCursor() != null && isShulkerBox(event.getCursor().getType()) &&
                checkMaxShulker(event.getClickedInventory(), player)) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(ChatColor.RED + "Unable to move shulker box!");
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
