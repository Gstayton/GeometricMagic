package me.kosannicholas.geometricmagic.Persistence;

import java.util.UUID;

public interface Persistence {
	void addUser(UUID uuid);

	String getUsername(UUID uuid);

	int getUserEnergy(UUID uuid);

	void setUserEnergy(UUID uuid, int energy);

	void shutdown();
}
