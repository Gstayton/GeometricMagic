/**
 * GeometricMagic allows players to draw redstone circles on the ground to do things such as teleport and transmute blocks.
 * Copyright (C) 2012, 2013, 2014  Alec Cox (cakenggt), Andrew Stevanus (Hoot215) <hoot893@gmail.com>, Nathan Thomas (Gstayton)
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

import net.h31ix.anticheat.api.AnticheatAPI;
import net.h31ix.anticheat.manage.CheckType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class GeometricMagicStorageCircle {
    public static void storageCircle(Location startLoc, Location endLoc, Player player, int size) {
        File folder = new File("plugins/GeometricMagic/storage/");
        File file = new File("plugins/GeometricMagic/storage/" + player.getName() + "." + String.valueOf(size));
        if (folder.exists()) {
            // Load blocks
            if (file.exists()) {
                try {
                    // exempt player from AntiCheat check
                    if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                        AnticheatAPI.exemptPlayer(player, CheckType.FAST_PLACE);
                        AnticheatAPI.exemptPlayer(player, CheckType.FAST_BREAK);
                        AnticheatAPI.exemptPlayer(player, CheckType.LONG_REACH);
                        AnticheatAPI.exemptPlayer(player, CheckType.NO_SWING);
                    }

                    storageCircleLoad(startLoc, endLoc, player, size, file);

                    // unexempt player from AntiCheat check
                    if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                        AnticheatAPI.unexemptPlayer(player, CheckType.FAST_PLACE);
                        AnticheatAPI.unexemptPlayer(player, CheckType.FAST_BREAK);
                        AnticheatAPI.unexemptPlayer(player, CheckType.LONG_REACH);
                        AnticheatAPI.unexemptPlayer(player, CheckType.NO_SWING);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            // Store blocks
            else {
                try {
                    // exempt player from AntiCheat check
                    if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                        AnticheatAPI.exemptPlayer(player, CheckType.FAST_PLACE);
                        AnticheatAPI.exemptPlayer(player, CheckType.FAST_BREAK);
                        AnticheatAPI.exemptPlayer(player, CheckType.LONG_REACH);
                        AnticheatAPI.exemptPlayer(player, CheckType.NO_SWING);
                    }

                    storageCircleStore(startLoc, endLoc, player, size, file);

                    // unexempt player from AntiCheat check
                    if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                        AnticheatAPI.unexemptPlayer(player, CheckType.FAST_PLACE);
                        AnticheatAPI.unexemptPlayer(player, CheckType.FAST_BREAK);
                        AnticheatAPI.unexemptPlayer(player, CheckType.LONG_REACH);
                        AnticheatAPI.unexemptPlayer(player, CheckType.NO_SWING);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Store blocks
            if (folder.mkdirs()) {
                try {
                    // exempt player from AntiCheat check
                    if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                        AnticheatAPI.exemptPlayer(player, CheckType.FAST_PLACE);
                        AnticheatAPI.exemptPlayer(player, CheckType.FAST_BREAK);
                        AnticheatAPI.exemptPlayer(player, CheckType.LONG_REACH);
                        AnticheatAPI.exemptPlayer(player, CheckType.NO_SWING);
                    }

                    storageCircleStore(startLoc, endLoc, player, size, file);

                    // unexempt player from AntiCheat check
                    if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                        AnticheatAPI.unexemptPlayer(player, CheckType.FAST_PLACE);
                        AnticheatAPI.unexemptPlayer(player, CheckType.FAST_BREAK);
                        AnticheatAPI.unexemptPlayer(player, CheckType.LONG_REACH);
                        AnticheatAPI.unexemptPlayer(player, CheckType.NO_SWING);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else
                GeometricMagicPlayerListener.plugin.getLogger().info("Error creating necessary folder(s)!" +
                        " Check your read/write permissions");
        }
    }

    public static void storageCircleLoad(Location startLoc, Location endLoc, Player player, int size, File file) throws FileNotFoundException {
        World world = player.getWorld();
        Scanner in = new Scanner(file);

        if (startLoc.getBlockX() > endLoc.getBlockX()) {
            if (startLoc.getBlockZ() > endLoc.getBlockZ()) {
                for (int x = startLoc.getBlockX(); x >= endLoc.getBlockX(); x--) {
                    for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                        for (int z = startLoc.getBlockZ(); z >= endLoc.getBlockZ(); z--) {
                            Location loc = new Location(world, x, y, z);
                            Block block = loc.getBlock();
                            String newBlockString = in.next();
                            String[] newBlockStringArray = newBlockString.split(",");
                            int newBlockID = Integer.parseInt(newBlockStringArray[0]);
                            byte newBlockData = Byte.parseByte(newBlockStringArray[1]);

                            // Block break
                            if (block.getTypeId() != 0 && newBlockID == 0) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId())) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                            // Block place
                            else if (block.getTypeId() == 0 && newBlockID != 0) {
                                if (!GeometricMagic.checkPlaceBlacklist(newBlockID)) {
                                    if (GeometricMagic.checkBlockPlaceSimulation(loc, newBlockID, newBlockData, loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                            // Block break and place
                            else if (block.getTypeId() != 0 && newBlockID != 0) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId()) && !GeometricMagic.checkPlaceBlacklist(newBlockID)) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player) && GeometricMagic.checkBlockPlaceSimulation(loc, newBlockID, newBlockData, loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                        }
                    }
                }
            } else {
                for (int x = startLoc.getBlockX(); x >= endLoc.getBlockX(); x--) {
                    for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                        for (int z = startLoc.getBlockZ(); z <= endLoc.getBlockZ(); z++) {
                            Location loc = new Location(world, x, y, z);
                            Block block = loc.getBlock();
                            String newBlockString = in.next();
                            String[] newBlockStringArray = newBlockString.split(",");
                            int newBlockID = Integer.parseInt(newBlockStringArray[0]);
                            byte newBlockData = Byte.parseByte(newBlockStringArray[1]);

                            // Block break
                            if (block.getTypeId() != 0 && newBlockID == 0) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId())) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                            // Block place
                            else if (block.getTypeId() == 0 && newBlockID != 0) {
                                if (!GeometricMagic.checkPlaceBlacklist(newBlockID)) {
                                    if (GeometricMagic.checkBlockPlaceSimulation(loc, newBlockID, newBlockData, loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                            // Block break and place
                            else if (block.getTypeId() != 0 && newBlockID != 0) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId()) && !GeometricMagic.checkPlaceBlacklist(newBlockID)) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player) && GeometricMagic.checkBlockPlaceSimulation(loc, newBlockID, newBlockData, loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (startLoc.getBlockZ() > endLoc.getBlockZ()) {
                for (int x = startLoc.getBlockX(); x <= endLoc.getBlockX(); x++) {
                    for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                        for (int z = startLoc.getBlockZ(); z >= endLoc.getBlockZ(); z--) {
                            Location loc = new Location(world, x, y, z);
                            Block block = loc.getBlock();
                            String newBlockString = in.next();
                            String[] newBlockStringArray = newBlockString.split(",");
                            int newBlockID = Integer.parseInt(newBlockStringArray[0]);
                            byte newBlockData = Byte.parseByte(newBlockStringArray[1]);

                            // Block break
                            if (block.getTypeId() != 0 && newBlockID == 0) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId())) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                            // Block place
                            else if (block.getTypeId() == 0 && newBlockID != 0) {
                                if (!GeometricMagic.checkPlaceBlacklist(newBlockID)) {
                                    if (GeometricMagic.checkBlockPlaceSimulation(loc, newBlockID, newBlockData, loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                            // Block break and place
                            else if (block.getTypeId() != 0 && newBlockID != 0) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId()) && !GeometricMagic.checkPlaceBlacklist(newBlockID)) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player) && GeometricMagic.checkBlockPlaceSimulation(loc, newBlockID, newBlockData, loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                        }
                    }
                }
            } else {
                for (int x = startLoc.getBlockX(); x <= endLoc.getBlockX(); x++) {
                    for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                        for (int z = startLoc.getBlockZ(); z <= endLoc.getBlockZ(); z++) {
                            Location loc = new Location(world, x, y, z);
                            Block block = loc.getBlock();
                            String newBlockString = in.next();
                            String[] newBlockStringArray = newBlockString.split(",");
                            int newBlockID = Integer.parseInt(newBlockStringArray[0]);
                            byte newBlockData = Byte.parseByte(newBlockStringArray[1]);

                            // Block break
                            if (block.getTypeId() != 0 && newBlockID == 0) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId())) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                            // Block place
                            else if (block.getTypeId() == 0 && newBlockID != 0) {
                                if (!GeometricMagic.checkPlaceBlacklist(newBlockID)) {
                                    if (GeometricMagic.checkBlockPlaceSimulation(loc, newBlockID, newBlockData, loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                            // Block break and place
                            else if (block.getTypeId() != 0 && newBlockID != 0) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId()) && !GeometricMagic.checkPlaceBlacklist(newBlockID)) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player) && GeometricMagic.checkBlockPlaceSimulation(loc, newBlockID, newBlockData, loc, player)) {
                                        block.setTypeId(newBlockID);
                                        block.setData(newBlockData);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                }
                            }
                        }
                    }
                }
            }
        }
        in.close();
        file.delete();
    }

    public static void storageCircleStore(Location startLoc, Location endLoc, Player player, int size, File file) throws FileNotFoundException {
        World world = player.getWorld();
        PrintWriter out = new PrintWriter(file);

        if (startLoc.getBlockX() > endLoc.getBlockX()) {
            if (startLoc.getBlockZ() > endLoc.getBlockZ()) {
                for (int x = startLoc.getBlockX(); x >= endLoc.getBlockX(); x--) {
                    for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                        for (int z = startLoc.getBlockZ(); z >= endLoc.getBlockZ(); z--) {
                            Location loc = new Location(world, x, y, z);
                            Block block = loc.getBlock();

                            if (block.getType() != Material.AIR) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId())) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player)) {
                                        out.println(String.valueOf(block.getTypeId()) + "," + String.valueOf(block.getData()));
                                        block.setType(Material.AIR);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                    out.println("0,0");
                                }
                            } else {
                                out.println("0,0");
                            }
                        }
                    }
                }
            } else {
                for (int x = startLoc.getBlockX(); x >= endLoc.getBlockX(); x--) {
                    for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                        for (int z = startLoc.getBlockZ(); z <= endLoc.getBlockZ(); z++) {
                            Location loc = new Location(world, x, y, z);
                            Block block = loc.getBlock();

                            if (block.getType() != Material.AIR) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId())) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player)) {
                                        out.println(String.valueOf(block.getTypeId()) + "," + String.valueOf(block.getData()));
                                        block.setType(Material.AIR);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                    out.println("0,0");
                                }
                            } else {
                                out.println("0,0");
                            }
                        }
                    }
                }
            }
        } else {
            if (startLoc.getBlockZ() > endLoc.getBlockZ()) {
                for (int x = startLoc.getBlockX(); x <= endLoc.getBlockX(); x++) {
                    for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                        for (int z = startLoc.getBlockZ(); z >= endLoc.getBlockZ(); z--) {
                            Location loc = new Location(world, x, y, z);
                            Block block = loc.getBlock();

                            if (block.getType() != Material.AIR) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId())) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player)) {
                                        out.println(String.valueOf(block.getTypeId()) + "," + String.valueOf(block.getData()));
                                        block.setType(Material.AIR);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                    out.println("0,0");
                                }
                            } else {
                                out.println("0,0");
                            }
                        }
                    }
                }
            } else {
                for (int x = startLoc.getBlockX(); x <= endLoc.getBlockX(); x++) {
                    for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                        for (int z = startLoc.getBlockZ(); z <= endLoc.getBlockZ(); z++) {
                            Location loc = new Location(world, x, y, z);
                            Block block = loc.getBlock();

                            if (block.getType() != Material.AIR) {
                                if (!GeometricMagic.checkBreakBlacklist(block.getTypeId())) {
                                    if (GeometricMagic.checkBlockBreakSimulation(loc, player)) {
                                        out.println(String.valueOf(block.getTypeId()) + "," + String.valueOf(block.getData()));
                                        block.setType(Material.AIR);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                    out.println("0,0");
                                }
                            } else {
                                out.println("0,0");
                            }
                        }
                    }
                }
            }
        }
        out.close();
    }
}
