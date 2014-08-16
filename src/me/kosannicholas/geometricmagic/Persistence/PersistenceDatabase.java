package me.kosannicholas.geometricmagic.Persistence;

import com.avaje.ebean.Query;
import me.kosannicholas.geometricmagic.GeometricMagicPlugin;
import org.bukkit.Bukkit;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.UUID;

public class PersistenceDatabase implements Persistence {
	private final GeometricMagicPlugin plugin;

	public PersistenceDatabase(GeometricMagicPlugin plugin) {
		this.plugin = plugin;
		checkDDL();
	}

	private void checkDDL() {
		try {
			plugin.getDatabase().find(EnergyStorage.class).findRowCount();
		} catch (PersistenceException e) {
			plugin.pluginInstallDDL();
		}
	}

	@Override
	public void addUser(UUID uuid) {
		Query<EnergyStorage> query = plugin.getDatabase().find(EnergyStorage.class);
		query.where().eq("uuid", uuid);
		query.setMaxRows(1);
		List<EnergyStorage> storage = query.findList();
		if (storage == null || storage.size() == 0) {
			EnergyStorage newEntry = plugin.getDatabase().createEntityBean(EnergyStorage.class);
			newEntry.setUuid(uuid);
			newEntry.setEnergy(0);
			newEntry.setUsername(Bukkit.getPlayer(uuid).getPlayerListName());
			plugin.getDatabase().save(newEntry);
		}
	}

	@Override
	public String getUsername(UUID uuid) {
		Query<EnergyStorage> query = plugin.getDatabase().find(EnergyStorage.class);
		query.where().eq("uuid", uuid);
		query.setMaxRows(1);
		List<EnergyStorage> storage = query.findList();
		if (storage == null || storage.size() == 0) {
			return null;
		} else {
			return storage.get(0).getUsername();
		}
	}

	public int getUserEnergy(UUID uuid) {
		Query<EnergyStorage> query = plugin.getDatabase().find(EnergyStorage.class);
		query.where().eq("uuid", uuid);
		query.setMaxRows(1);
		List<EnergyStorage> storage = query.findList();
		if (storage == null || storage.size() == 0) {
			return 0;
		} else {
			return storage.get(0).getEnergy();
		}
	}

	public void setUserEnergy(UUID uuid, int energy) {
		Query<EnergyStorage> query = plugin.getDatabase().find(EnergyStorage.class);
		query.where().eq("uuid", uuid);
		query.setMaxRows(1);
		List<EnergyStorage> storage = query.findList();

		storage.get(0).setEnergy(energy);
		plugin.getDatabase().save(storage);
	}

	@Override
	public void shutdown() {

	}
}
