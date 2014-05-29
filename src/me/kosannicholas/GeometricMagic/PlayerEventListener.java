package me.kosannicholas.GeometricMagic;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerEventListener implements Listener{
    private final GeometricMagicPlugin plugin;

    public PlayerEventListener(GeometricMagicPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                plugin.getLogger().info("Right clicked block!");
        }
    }
}
