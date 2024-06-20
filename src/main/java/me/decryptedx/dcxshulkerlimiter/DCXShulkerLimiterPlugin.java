package me.decryptedx.dcxshulkerlimiter;

import net.luckperms.api.LuckPerms;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DCXShulkerLimiterPlugin extends JavaPlugin {
    FileConfiguration config = getConfig();
    private InventoryListener inventoryListener;
    private static LuckPerms luckPerms;

    @Override
    public void onEnable() {
        config.addDefault("PlayerShulkerLimit", -1);
        config.addDefault("EnderChestShulkerLimit", -1);
        config.addDefault("OtherShulkerLimit", -1);
        config.options().copyDefaults(true);
        saveConfig();

        inventoryListener = new InventoryListener(config);

        final RegisteredServiceProvider<LuckPerms> rsp = getServer().getServicesManager().getRegistration(LuckPerms.class);
        luckPerms = rsp.getProvider(); // should never return null, LP is depended on by this plugin.

        getServer().getPluginManager().registerEvents(inventoryListener, this);
    }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(inventoryListener);
    }
}
