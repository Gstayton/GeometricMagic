package me.kosannicholas.GeometricMagic;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class GeometricMagic extends JavaPlugin {
    public static GeometricMagic plugin;

    @Override
    public void onDisable() {
        System.out.println(this + " is now disabled!");
        plugin = null;
    }

    @Override
    public void onEnable() {
        startPluginMetrics();

        this.getLogger().info("Plugin is enabled");

        // Start auto-update if applicable
        if (getConfig().getBoolean("autoUpdate")) {
            Updater.UpdateType updateType = null;
            if (getConfig().getString("updateType").toLowerCase() == "default") {
                updateType = Updater.UpdateType.DEFAULT;
            } else if (getConfig().getString("updateType").toLowerCase() == "no_download") {
                updateType = Updater.UpdateType.NO_DOWNLOAD;
            } else if (getConfig().getString("updateType").toLowerCase() == "no_version_check") {
                updateType = Updater.UpdateType.NO_VERSION_CHECK;
            } else {
                updateType = Updater.UpdateType.NO_DOWNLOAD;
            }
            Updater updater = new Updater(this, 40378, this.getFile(), updateType, false);
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