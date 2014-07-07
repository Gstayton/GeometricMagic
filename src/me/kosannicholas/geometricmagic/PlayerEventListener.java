package me.kosannicholas.geometricmagic;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

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
                Block act = event.getClickedBlock();
                plugin.getLogger().info("Block coordinates: " + act.getLocation());
                plugin.getLogger().info("Chunk coordinates: " + act.getChunk().getX() + " - " + act.getChunk().getZ());
                plugin.getLogger().info("Chunk bitshifted coordinates: " + (act.getChunk().getX() << 4) + " - " + (act.getChunk().getZ() << 4));
                break;
        }
    }
}