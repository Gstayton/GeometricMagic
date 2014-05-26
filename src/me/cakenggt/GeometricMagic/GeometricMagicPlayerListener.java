/**
 * GeometricMagic allows players to draw redstone circles on the ground to do things such as teleport and transmute blocks.
 * Copyright (C) 2012  Alec Cox (cakenggt), Andrew Stevanus (Hoot215) <hoot893@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cakenggt.GeometricMagic;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;

public class GeometricMagicPlayerListener implements Listener {
    public static Economy economy = null;
    public static HashMap<String, Long> mapCoolDowns = new HashMap<String, Long>();
    static GeometricMagic plugin;
    public GeometricMagicPlayerListener(GeometricMagic instance) {
        plugin = instance;
    }

    public static void alchemyFiller(Material a, byte fromData, Material b, byte toData, Location start, Location end, Player player, boolean charge) {
        // System.out.println("alchemyFiller");
        String playerName = player.getName();
        long rate = plugin.getConfig().getLong("transmutation.rate");

        new Thread(new GeometricMagicTransmutationThread(plugin, rate, a, fromData, b, toData, start, end, playerName, charge)).start();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayLogin(PlayerLoginEvent event) {
        final String playerName = event.getPlayer().getName();

        if (event.getPlayer().hasPermission("geometricmagic.notify")) {
            if (!plugin.upToDate && plugin.getConfig().getBoolean("general.auto-update-notify")) {
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                    public void run() {
                        if (plugin.getServer().getPlayer(playerName) != null) {
                            plugin.getServer().getPlayer(playerName).sendMessage(
                                    ChatColor.GREEN
                                            + "A newer version of" + ChatColor.RED +
                                            " GeometricMagic" + ChatColor.GREEN
                                            + " is available!"
                            );
                            plugin.getServer().getPlayer(playerName).sendMessage(
                                    ChatColor.GREEN
                                            + "If you cannot find a newer version," +
                                            " check in the comments for a Dropbox link"
                            );
                        }
                    }
                }, 60L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {

        // System.out.println("is playerinteractevent");
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            // System.out.println("doesn't equal click block or click air");
            return;
        }

        Player player = event.getPlayer();

        boolean sacrificed = false;

        if (!player.hasPermission("geometricmagic.bypass.sacrifice")) {
            try {
                sacrificed = GeometricMagic.checkSacrificed(player);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        boolean sacrifices = false;
        try {
            sacrifices = GeometricMagic.checkSacrifices(player);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Block actBlock = player.getLocation().getBlock();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.WORKBENCH && (sacrifices && !sacrificed) && !player.hasPermission("geometricmagic.bypass.crafting")) {
                // cancel event instead of turning block into air
                player.sendMessage("You have already sacrificed your crafting abilities. You must sacrifice your alchemy forever to get them back by performing another human transmutation.");
                event.setCancelled(true);
            }
            actBlock = event.getClickedBlock();
        }

        ItemStack inHand = event.getPlayer().getItemInHand();
        Material inHandType = inHand.getType();

        if ((sacrificed && (inHandType == Material.FLINT || (actBlock.getType() == Material.REDSTONE_WIRE && inHand.getAmount() == 0))) && !player.hasPermission("geometricmagic.bypass.sacrifice")) {
            player.sendMessage("You have sacrificed your alchemy abilities forever.");
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR && inHandType == Material.FLINT) {
            actBlock = player.getTargetBlock(null, 120);
        }

        World world = player.getWorld();
        try {
            GeometricMagic.isCircle(player, world, actBlock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
