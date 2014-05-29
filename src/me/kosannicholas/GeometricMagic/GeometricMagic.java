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

    }

    private void startPluginMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().warning("Failed to start Metrics");
            // Failed to submit the stats :-(
        }
    }
}