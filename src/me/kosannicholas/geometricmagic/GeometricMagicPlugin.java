package me.kosannicholas.geometricmagic;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class GeometricMagicPlugin extends JavaPlugin {
	@Override
	public void onDisable() {
		getLogger().info(this + " is now disabled!");
	}

	@Override
	public void onEnable() {
		getLogger().info("Plugin is enabled");

		// Start auto-update if applicable
		if (getConfig().getBoolean("autoUpdate")) {
			String updateTypeValue = getConfig().getString("updateType").toUpperCase();
			Updater.UpdateType updateType = Updater.UpdateType.valueOf(updateTypeValue);
			Updater updater = new Updater(this, 40378, getFile(), updateType, false);
		}

		startPluginMetrics();

		PluginManager mgr = getServer().getPluginManager();
		mgr.registerEvents(new PlayerEventListener(this), this);

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