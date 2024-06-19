package me.decryptedx.dcxshulkerlimiter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class DCXShulkerLimiterPlugin extends JavaPlugin {
    FileConfiguration config = getConfig();
    private InventoryListener inventoryListener;

    @Override
    public void onEnable() {
        config.addDefault("PlayerShulkerLimit", -1);
        config.addDefault("EnderChestShulkerLimit", -1);
        config.addDefault("OtherShulkerLimit", -1);
        config.addDefault("AdditivePermission", false);
        config.options().copyDefaults(true);
        saveConfig();

        inventoryListener = new InventoryListener(config);
        getServer().getPluginManager().registerEvents(inventoryListener, this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(inventoryListener);
    }
}
