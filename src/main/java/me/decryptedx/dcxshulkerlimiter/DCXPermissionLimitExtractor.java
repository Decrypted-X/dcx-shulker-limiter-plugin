package me.decryptedx.dcxshulkerlimiter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.logging.Level;

/**
 * Helper class that encapsulates getting a player's highest effective permission for a permission node
 * that starts with a prefix
 */
public class DCXPermissionLimitExtractor {
    private final Player player;

    public DCXPermissionLimitExtractor(final Player player) {
        this.player = player;
    }

    /**
     *
     * @param prefix Permission node prefix
     * @param defaultValue Default value of permission
     * @return Highest value of permission nodes in the player's permission with a given prefix
     */
    public int getLimit(String prefix, int defaultValue) {
        if (player == null) return defaultValue;

        try {
            return player.getEffectivePermissions().stream()
                         .map(PermissionAttachmentInfo::getPermission)
                         .filter(permission -> permission.startsWith(prefix))
                         .mapToInt(InventoryListener::integerValueFromPermission)
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
