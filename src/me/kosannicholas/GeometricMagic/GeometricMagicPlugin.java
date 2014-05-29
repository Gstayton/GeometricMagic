package me.kosannicholas.GeometricMagic;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class GeometricMagicPlugin extends JavaPlugin {
    public static GeometricMagicPlugin plugin;

    @Override
    public void onDisable() {
        System.out.println(this + " is now disabled!");
        plugin = null;
    }

    @Override
    public void onEnable() {
        startPluginMetrics();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerEventListener(this), this);

        this.getLogger().info("Plugin is enabled");

        // Start auto-update if applicable
        if (getConfig().getBoolean("autoUpdate")) {
            String updateTypeValue = getConfig().getString("updateType").toUpperCase();
            Updater.UpdateType updateType = Updater.UpdateType.valueOf(updateTypeValue);
            Updater updater = new Updater(this, 40378, getFile(), updateType, false);
        }
    }

    private void startPluginMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().warning("Failed to start Metrics: " + e);
            // Failed to submit the stats :-(
        }
    }
}