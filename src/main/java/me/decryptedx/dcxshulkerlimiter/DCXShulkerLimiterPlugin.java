package me.decryptedx.dcxshulkerlimiter;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class DCXShulkerLimiterPlugin extends JavaPlugin {
    private InventoryListener inventoryListener;

    @Override
    public void onEnable() {
        inventoryListener = new InventoryListener();
        getServer().getPluginManager().registerEvents(inventoryListener, this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(inventoryListener);
    }
}
