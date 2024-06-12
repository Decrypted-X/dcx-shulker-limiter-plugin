package me.decryptedx.dcxshulkerlimiter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.logging.Level;

/**
 * Helper class that encapsulates getting a player's highest effective permission for a permission node
 * that starts with a prefix.
 */
public class PermissionLimitExtractor {
    /**Get the integer value of a permission.
     *
     * @param permission The permission to get the integer value from.
     * @return An integer that is the value retrieved from the permission.
     * @throws NumberFormatException Thrown if permission contains invalid value.
     * @throws IndexOutOfBoundsException Thrown if permission is in invalid format.
     */
    public static int integerValueFromPermission(String permission)
            throws NumberFormatException, IndexOutOfBoundsException {
        return Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
    }

    /**Get limit from player permission node or default value.
     *
     * @param player The player to get the permission from.
     * @param prefix Permission node prefix.
     * @param defaultValue Default value of permission.
     * @return Highest value of permission nodes in the player's permission with a given prefix.
     */
    public static int getLimit(Player player, String prefix, int defaultValue) {
        if (player == null)
            return defaultValue;

        try {
            return player.getEffectivePermissions().stream()
                         .map(PermissionAttachmentInfo::getPermission)
                         .filter(permission -> permission.startsWith(prefix))
                         .mapToInt(PermissionLimitExtractor::integerValueFromPermission)
                         .max()
                         .orElse(defaultValue);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                    "Permission node " + prefix + " for " + player.getName() +
                    " has a non-integer value, returning default.");
            return defaultValue;
        }
    }
}
