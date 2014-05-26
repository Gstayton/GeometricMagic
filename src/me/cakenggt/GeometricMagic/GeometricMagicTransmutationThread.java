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
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Array;

public class GeometricMagicTransmutationThread implements Runnable {
	private GeometricMagic plugin;
	private long rate;
	private Material a;
	private byte fromData;
	private Material b;
	private byte toData;
	private Location start;
	private Location end;
	private String playerName;
	private boolean charge;
	
	public GeometricMagicTransmutationThread(GeometricMagic instance,
			long rateLong,
			Material aMat,
			byte fromDataByte,
			Material bMat,
			byte toDataByte,
			Location startLoc,
			Location endLoc,
			String playerNameString,
			boolean chargeBool) {
		plugin = instance;
		rate = rateLong;
		a = aMat;
		fromData = fromDataByte;
		b = bMat;
		toData = toDataByte;
		start = startLoc;
		end = endLoc;
		playerName = playerNameString;
		charge = chargeBool;
	}
	
	public void run() {
        // transmuteArea()
        main();
	}

    public void main() {
        if (start.getX() < end.getX()) {
            if (start.getZ() < end.getZ()) {
                for (int x = (int) start.getX(); x <= end.getX(); x++) {
                    for (int y = (int) start.getY(); y <= end.getY(); y++) {
                        for (int z = (int) start.getZ(); z <= end.getZ(); z++) {
                            Location loc = new Location(start.getWorld(), (double) x, (double) y, (double) z);
                            transmuteBlock(a, fromData, b, toData, loc, playerName, charge);
                            try {
                                Thread.sleep(rate);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            else {
                for (int x = (int) start.getX(); x <= end.getX(); x++) {
                    for (int y = (int) start.getY(); y <= end.getY(); y++) {
                        for (int z = (int) start.getZ(); z >= end.getZ(); z--) {
                            Location loc = new Location(start.getWorld(), (double) x, (double) y, (double) z);
                            transmuteBlock(a, fromData, b, toData, loc, playerName, charge);
                            try {
                                Thread.sleep(rate);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        else {
            if (start.getZ() < end.getZ()) {
                for (int x = (int) start.getX(); x >= end.getX(); x--) {
                    for (int y = (int) start.getY(); y <= end.getY(); y++) {
                        for (int z = (int) start.getZ(); z <= end.getZ(); z++) {
                            Location loc = new Location(start.getWorld(), (double) x, (double) y, (double) z);
                            transmuteBlock(a, fromData, b, toData, loc, playerName, charge);
                            try {
                                Thread.sleep(rate);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            else {
                for (int x = (int) start.getX(); x >= end.getX(); x--) {
                    for (int y = (int) start.getY(); y <= end.getY(); y++) {
                        for (int z = (int) start.getZ(); z >= end.getZ(); z--) {
                            Location loc = new Location(start.getWorld(), (double) x, (double) y, (double) z);
                            transmuteBlock(a, fromData, b, toData, loc, playerName, charge);
                            try {
                                Thread.sleep(rate);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
	
	public void transmuteBlock(final Material a, final byte fromData, final Material b, final byte toData,
										final Location startBlockLoc, final String playerName, final boolean charge) {
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Player player = plugin.getServer().getPlayer(playerName);
				Block startBlock = startBlockLoc.getBlock();
				double pay = GeometricMagicPlayerListener.calculatePay(a, fromData, b, toData, player);
				
				// exempt player from AntiCheat check
				if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
					AnticheatAPI.exemptPlayer(player, CheckType.FAST_PLACE);
					AnticheatAPI.exemptPlayer(player, CheckType.FAST_BREAK);
					AnticheatAPI.exemptPlayer(player, CheckType.LONG_REACH);
					AnticheatAPI.exemptPlayer(player, CheckType.NO_SWING);
				}
				
				if (player != null) {
					if (startBlock.getType() == a && startBlock.getData() == fromData) {

						if (-1 * GeometricMagicPlayerListener.getBalance(player) < pay || !charge) {

							// Block break
							if (a != Material.AIR && b == Material.AIR) {
								
								if (!GeometricMagicPlayerListener.checkBreakBlacklist(a.getId())) {
									
									Location blockLocation = startBlock.getLocation();

									if (GeometricMagicPlayerListener.checkBlockBreakSimulation(blockLocation, player)) {
										// Change block
										startBlock.setType(b);
										if (toData != 0)
											startBlock.setData(toData);

										if (charge) {
											if (GeometricMagicPlayerListener.getTransmutationCostSystem(plugin).equalsIgnoreCase("vault")) {

												Economy econ = GeometricMagic.getEconomy();

												// Deposit or withdraw to players Vault account
												if (pay > 0) {
													econ.depositPlayer(player.getName(), pay);
												} else if (pay < 0) {
													econ.withdrawPlayer(player.getName(), pay * -1);
												}
											} else if (GeometricMagicPlayerListener.getTransmutationCostSystem(plugin).equalsIgnoreCase("xp")) {
												player.setLevel((int) (player.getLevel() + pay));
											}
										}
									}
								}
								
								else {
									player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
									return;
								}
							}

							// Block place
							else if (a == Material.AIR && b != Material.AIR) {
								
								if (!GeometricMagicPlayerListener.checkPlaceBlacklist(b.getId())) {
									
									Location blockLocation = startBlock.getLocation();
									int blockID = b.getId();
									byte blockData = toData;

									if (GeometricMagicPlayerListener.checkBlockPlaceSimulation(blockLocation, blockID, blockData, blockLocation, player)) {
										// Change block
										startBlock.setType(b);
										if (toData != 0)
											startBlock.setData(toData);

										if (charge) {
											if (GeometricMagicPlayerListener.getTransmutationCostSystem(plugin).equalsIgnoreCase("vault")) {

												Economy econ = GeometricMagic.getEconomy();

												// Deposit or withdraw to players Vault account
												if (pay > 0) {
													econ.depositPlayer(player.getName(), pay);
												} else if (pay < 0) {
													econ.withdrawPlayer(player.getName(), pay * -1);
												}
											} else if (GeometricMagicPlayerListener.getTransmutationCostSystem(plugin).equalsIgnoreCase("xp")) {
												player.setLevel((int) (player.getLevel() + pay));
											}
										}
									}
								}
								
								else {
									player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
									return;
								}
							}

							// Block break and place
							else if (a != Material.AIR && b != Material.AIR) {

								if (!GeometricMagicPlayerListener.checkBreakBlacklist(a.getId()) && !GeometricMagicPlayerListener.checkPlaceBlacklist(b.getId())) {
									
									Location blockLocation = startBlock.getLocation();
									int blockID = b.getId();
									byte blockData = toData;

									if (GeometricMagicPlayerListener.checkBlockBreakSimulation(blockLocation, player)
											&& GeometricMagicPlayerListener.checkBlockPlaceSimulation(blockLocation, blockID, blockData, blockLocation, player)) {
										// Change block
										startBlock.setType(b);
										if (toData != 0)
											startBlock.setData(toData);

										if (charge) {
											if (GeometricMagicPlayerListener.getTransmutationCostSystem(plugin).equalsIgnoreCase("vault")) {

												Economy econ = GeometricMagic.getEconomy();

												// Deposit or withdraw to players Vault account
												if (pay > 0) {
													econ.depositPlayer(player.getName(), pay);
												} else if (pay < 0) {
													econ.withdrawPlayer(player.getName(), pay * -1);
												}
											} else if (GeometricMagicPlayerListener.getTransmutationCostSystem(plugin).equalsIgnoreCase("xp")) {
												player.setLevel((int) (player.getLevel() + pay));
											}
										}
									}
								}
								
								else {
									player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
									return;
								}
								
							}
						} else {
							player.sendMessage(ChatColor.RED + "You do not have enough power to create that block");
							return;
						}
					} else
						// System.out.println("[GeometricMagic] DEBUG - Block Data: " + (int) startBlock.getData() + ", A Data: " + (int) fromData + ", B Data: " + (int) toData);
						return;
				}
				
				// unexempt player from AntiCheat check
				if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
					AnticheatAPI.unexemptPlayer(player, CheckType.FAST_PLACE);
					AnticheatAPI.unexemptPlayer(player, CheckType.FAST_BREAK);
					AnticheatAPI.unexemptPlayer(player, CheckType.LONG_REACH);
					AnticheatAPI.unexemptPlayer(player, CheckType.NO_SWING);
				}
			}
		}, 0L);
	}

    private static Location[] sortCorners(Location[] corners) {
        int j;
        boolean flag = true;
        int temp;
        Location tempL;
        int num[] = new int[0];

        for (int i = 0; i < corners.length; i++) {
            num[i] = (corners[i].getBlockX() - corners[i].getBlockZ());
        }

        while (flag) {
            flag = false;
            for (j = 0; j < corners.length - 1; j++) {
                if (num[j] > num[j + 1]) {
                    temp = num[j];
                    num[j] = num[j + 1];
                    num[j + 1] = temp;

                    tempL = corners[j];
                    corners[j] = corners[j + 1];
                    corners[j + 1] = tempL;

                    flag = true;
                }
            }
        }
        return corners;
    }

    public boolean transmuteArea(Location[] templateCorners, Location[] areaCorners, MaterialData fromData, MaterialData toData, Player player) {
        plugin.getLogger().info("transmuteArea");

        if (Array.getLength(templateCorners) != 2 || Array.getLength(areaCorners) != 2) {
            throw new IllegalArgumentException("Expected array length of 2");
        }

        templateCorners[2] = new Location(templateCorners[0].getWorld(), templateCorners[0].getX(), templateCorners[0].getY(), templateCorners[1].getZ());
        templateCorners[3] = new Location(templateCorners[0].getWorld(), templateCorners[1].getX(), templateCorners[0].getY(), templateCorners[0].getZ());

        templateCorners = sortCorners(templateCorners);
        areaCorners = sortCorners(areaCorners);

        int templateSize = (int) templateCorners[0].distance(templateCorners[1]);

        Location areaPointer = areaCorners[0];

        Location templatePointer = templateCorners[0];

        for (int i = 0; i < templateSize; i++) {
            for (int j = 0; j < templateSize; j++) {
                for (int k = 0; k < templateSize; k++) {
                    if (!templatePointer.getBlock().isEmpty()) {
                        cubicReplace(areaPointer, templateSize, fromData, toData, player);
                    }
                    // increment x
                    templatePointer = templatePointer.getBlock().getRelative(BlockFace.WEST).getLocation();
                    areaPointer = areaPointer.getBlock().getRelative(BlockFace.WEST, templateSize + 1).getLocation();
                }
                //increment z
                templatePointer = templatePointer.getBlock().getRelative(BlockFace.NORTH).getLocation();
                areaPointer = areaPointer.getBlock().getRelative(BlockFace.NORTH, templateSize + 1).getLocation();
            }
            // increment y
            templatePointer = templatePointer.getBlock().getRelative(BlockFace.UP).getLocation();
            areaPointer = areaPointer.getBlock().getRelative(BlockFace.UP, templateSize + 1).getLocation();
        }

        return true;
    }

    /**
     * Replaces selected blocks in a cubic section of the world. <i>pointer</i> needs to be the South-East
     * corner of the block for this function to work.
     *
     * @param pointer  Starting point, assumed to be SE corner
     * @param size     Size of a single side of the cube desired
     * @param fromData Filter for blocks to be replaced
     * @param toData   Data that selected blocks will be replaced with
     * @param player   Player that will be charged for replacing
     * @return Number of blocks replaced
     */
    public int cubicReplace(Location pointer, int size, MaterialData fromData, MaterialData toData, Player player) {

        int blocks = 0;

        double pay = GeometricMagicPlayerListener.calculatePay(
                fromData.getItemType(),
                fromData.getData(),
                toData.getItemType(),
                toData.getData(),
                player
        );

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    plugin.getLogger().info("replace block");
                    if (pointer.getBlock().getType() == fromData.getItemType() && (-1 * GeometricMagicPlayerListener.getBalance(player) < pay)){
                        GeometricMagicPlayerListener.playerTransaction(player, pay);
                        pointer.getBlock().setType(toData.getItemType());
                        blocks++;
                    }
                    // increment x
                    pointer = pointer.getBlock().getRelative(BlockFace.WEST).getLocation();
                }
                // increment z
                pointer = pointer.getBlock().getRelative(BlockFace.NORTH).getLocation();
            }
            // increment y
            pointer = pointer.getBlock().getRelative(BlockFace.UP).getLocation();
        }
        return blocks;
    }
}