package me.kosannicholas.geometricmagic;

import me.kosannicholas.geometricmagic.Persistence.EnergyStorage;
import me.kosannicholas.geometricmagic.Persistence.Persistence;
import me.kosannicholas.geometricmagic.Persistence.PersistenceDatabase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class GeometricMagicPlugin extends JavaPlugin {
	private Persistence persistence;
	private static GeometricMagicPlugin instance = null;

	private FileConfiguration energyValues = null;

	public static GeometricMagicPlugin getInstance() {
		return instance;
	}

	@Override
	public void onDisable() {
		persistence.shutdown();
		persistence = null;
		removeDDL();

		instance = null;
		getLogger().info(this + " is now disabled!");
	}

	public Persistence getDatabaseHandler() {
		return persistence;
	}

	public void pluginInstallDDL() {
		installDDL();
	}

	@Override
	public void onEnable() {
		instance = this;
		getLogger().info("Plugin is enabled");

		// Start auto-update if applicable
		if (getConfig().getBoolean("autoUpdate")) {
			String updateTypeValue = getConfig().getString("updateType").toUpperCase();
			Updater.UpdateType updateType = Updater.UpdateType.valueOf(updateTypeValue);
			Updater updater = new Updater(this, 40378, getFile(), updateType, false);
		}

		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();

		startPluginMetrics();

		persistence = new PersistenceDatabase(this);

		PluginManager mgr = getServer().getPluginManager();
		mgr.registerEvents(new PlayerEventListener(this), this);

		energyValues = new EnergyValues().getEnergyValues();
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> classes = new LinkedList<Class<?>>();

		classes.add(EnergyStorage.class);
		return classes;
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

	@Override
	public boolean onCommand(final CommandSender sender,
													 final Command command,
													 final String label,
													 final String[] args) {
		if (command.getName().equalsIgnoreCase("testenergy")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				int energy = persistence.getUserEnergy(player.getUniqueId());
				getLogger().info(Integer.toString(energy));
				player.sendMessage(Integer.toString(energy));
			}
		}
		if (command.getName().equalsIgnoreCase("testusername")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				String username = persistence.getUsername(player.getUniqueId());
				getLogger().info(username);
				player.sendMessage(username);
			}
		}
		if (command.getName().equalsIgnoreCase("addenergy")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				int energy = persistence.getUserEnergy(player.getUniqueId());
				int amount = Integer.parseInt(args[0]);
				int total = energy + amount;
				getLogger().info(String.valueOf(total));
				persistence.setUserEnergy(player.getUniqueId(), total);
			}
		}
		if (command.getName().equalsIgnoreCase("getEnergyValue")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				player.sendMessage(energyValues.get(args[0]).toString());
			}
		}
		return true;
	}
}