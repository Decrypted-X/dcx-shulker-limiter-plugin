package me.decryptedx.dcxshulkerlimiter;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

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

        return player.getEffectivePermissions().stream()
                     .map(PermissionAttachmentInfo::getPermission)
                     .filter(permission -> permission.startsWith(prefix))
                     .mapToInt(InventoryListener::integerValueFromPermission)
                     .max()
                     .orElse(defaultValue);
    }
}
