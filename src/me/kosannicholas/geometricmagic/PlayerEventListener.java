package me.kosannicholas.geometricmagic;

import me.kosannicholas.geometricmagic.Persistence.Persistence;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEventListener implements Listener {
	private final GeometricMagicPlugin plugin;

	public PlayerEventListener(GeometricMagicPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		switch (event.getAction()) {
			case RIGHT_CLICK_BLOCK:
				Player player = event.getPlayer();
				if (player.getItemInHand().getType().equals(Material.AIR) &
						event.getClickedBlock().getType().equals(Material.REDSTONE_WIRE)) {
					TransmutationCircle tc = new TransmutationCircle(plugin);
					tc.search(event.getClickedBlock());
					if (tc.foundCircle()) {
						plugin.getLogger().info("Found circle: " + tc.getInnerWidth());
					}
					Block act = event.getClickedBlock();
					plugin.getLogger().info("Block coordinates: " + act.getLocation());
				}
				break;
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer() != null) {
			Player player = event.getPlayer();
			player.sendMessage("Welcome " + player.getUniqueId() + "!");
			Persistence db = plugin.getDatabaseHandler();
			db.addUser(player.getUniqueId());
		}
	}
}