package me.kosannicholas.geometricmagic;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;

public class PlayerEventListener implements Listener {
	private final GeometricMagicPlugin plugin;

	public PlayerEventListener(GeometricMagicPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		switch (event.getAction()) {
			case RIGHT_CLICK_BLOCK:
				TransmutationCircle tc = new TransmutationCircle(plugin);
				tc.search(event.getClickedBlock());
				if (tc.foundCircle()) {
					plugin.getLogger().info("Found circle, inner-width: " + tc.getInnerWidth());
				}
				break;
		}
	}
}