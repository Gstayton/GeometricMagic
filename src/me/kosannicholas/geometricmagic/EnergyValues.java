package me.kosannicholas.geometricmagic;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public class EnergyValues {
	private FileConfiguration energyValuesConfig = null;
	private File energyValuesFile = null;
	private GeometricMagicPlugin instance = GeometricMagicPlugin.getInstance();

	public void reloadEnergyValuesConfig() {
		Reader defConfigStream = null;
		if (energyValuesConfig == null) {
			energyValuesFile = new File(instance.getDataFolder(), "energy-values.yml");
		}
		energyValuesConfig = YamlConfiguration.loadConfiguration(energyValuesFile);

		try {
			defConfigStream = new InputStreamReader(instance.getResource("energy-values.yml"), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			energyValuesConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getEnergyValues() {
		if (energyValuesConfig == null) {
			reloadEnergyValuesConfig();
		}
		return energyValuesConfig;
	}

	public void SaveCustomConfig() {
		if (energyValuesConfig == null || energyValuesFile == null) {
			return;
		}
		try {
			getEnergyValues().save(energyValuesFile);
		} catch (IOException ex) {
			instance.getLogger().log(Level.SEVERE, "Could not save config to " + energyValuesFile, ex);
		}
	}

	public void saveDefaultEnergyValues() {
		if (energyValuesFile == null) {
			energyValuesFile = new File(instance.getDataFolder(), "energy-values.yml");
		}
		if (!energyValuesFile.exists()) {
			instance.saveResource("energy-values.yml", false);
		}
	}
}
