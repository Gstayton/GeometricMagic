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
import org.bukkit.*;
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

    public static void transmutationCircle(Player player, World world, Block actBlock, int transmutationCircleSize, int storageCircleSize) {
        int halfWidth = 0;
        int fullWidth = 0;
        Location startLoc = actBlock.getLocation();
        Location endLoc = actBlock.getLocation();
        Location circleStart = actBlock.getLocation();
        Location circleEnd = actBlock.getLocation();
        Material fromType = actBlock.getType();
        Material toType = actBlock.getType();
        Location[] templateCorners = new Location[0];
        boolean lightning = false;

        BlockFace direction = null;
        BlockFace check = null;
        Block currentBlock;
        int numCorners = 0;

        currentBlock = actBlock;

        if (actBlock.getRelative(0, 0, -1).getType() == Material.REDSTONE_WIRE && actBlock.getRelative(0, 0, 1).getType() == Material.REDSTONE_WIRE) {
            halfWidth = 0;
            while (actBlock.getRelative(0, 0, -1 * halfWidth).getType() == Material.REDSTONE_WIRE) {
                halfWidth++;
            }
            fullWidth = (halfWidth * 2) - 1;
            int dimensionOfEffect = (fullWidth - 2) * (fullWidth - 2);
            if (actBlock.getRelative((fullWidth - 1), 0, 0).getType() == Material.REDSTONE_WIRE) {
                // east
                if (actBlock.getRelative((halfWidth - 1), 0, halfWidth).getType() == Material.REDSTONE_WIRE
                        && actBlock.getRelative((halfWidth - 1), 0, (-1 * halfWidth)).getType() == Material.REDSTONE_WIRE) {
                    if (fullWidth - 2 > transmutationCircleSize) {
                        return;
                    }
                    Block fromBlock = actBlock.getLocation().add(halfWidth - 1, 0, -1 * (halfWidth + 1)).getBlock();
                    Block toBlock = actBlock.getLocation().add(halfWidth - 1, 0, halfWidth + 1).getBlock();
                    fromType = fromBlock.getType();
                    toType = toBlock.getType();
                    byte fromData = fromBlock.getData();
                    byte toData = toBlock.getData();
                    startLoc = actBlock.getLocation().add(fullWidth, 0, -1 * dimensionOfEffect / 2);
                    // System.out.println(startLoc);
                    endLoc = actBlock.getLocation().add(fullWidth + dimensionOfEffect - 1, dimensionOfEffect - 1, dimensionOfEffect / 2 - 1);
                    // System.out.println(endLoc);
                    circleStart = actBlock.getLocation().add(1, 0, -1 * (halfWidth - 2));
                    // System.out.println(circleStart);
                    circleEnd = actBlock.getLocation().add(fullWidth - 2, fullWidth - 3, halfWidth - 2);
                    // System.out.println(circleEnd);
                    alchemyCheck(fromType, fromData, toType, toData, circleStart, circleEnd, startLoc, endLoc, player, fullWidth - 2);
                    lightning = true;
                }
                // Storage circle
                else {
                    if (fullWidth - 2 > storageCircleSize) {
                        return;
                    }
                    startLoc = actBlock.getLocation().add(1, 0, (-1 * (halfWidth - 2)));
                    endLoc = actBlock.getLocation().add((fullWidth - 2), (fullWidth - 3), (halfWidth - 2));
                    GeometricMagicStorageCircle.storageCircle(startLoc, endLoc, player, (fullWidth - 2));
                    lightning = true;
                }
            } else if (actBlock.getRelative(-1 * (fullWidth - 1), 0, 0).getType() == Material.REDSTONE_WIRE) {
                // west
                // System.out.println("transmutationCircle west");
                if (actBlock.getRelative((-1 * (halfWidth - 1)), 0, halfWidth).getType() == Material.REDSTONE_WIRE
                        && actBlock.getRelative((-1 * (halfWidth - 1)), 0, (-1 * halfWidth)).getType() == Material.REDSTONE_WIRE) {
                    if (fullWidth - 2 > transmutationCircleSize) {
                        return;
                    }
                    Block fromBlock = actBlock.getLocation().add(-1 * (halfWidth - 1), 0, halfWidth + 1).getBlock();
                    Block toBlock = actBlock.getLocation().add((-1) * (halfWidth - 1), 0, (-1) * (halfWidth + 1)).getBlock();
                    fromType = fromBlock.getType();
                    toType = toBlock.getType();
                    byte fromData = fromBlock.getData();
                    byte toData = toBlock.getData();
                    startLoc = actBlock.getLocation().add(-1 * fullWidth, 0, dimensionOfEffect / 2);
                    endLoc = actBlock.getLocation().add(-1 * (fullWidth + dimensionOfEffect) + 1, dimensionOfEffect - 1, -1 * dimensionOfEffect / 2 + 1);
                    circleStart = actBlock.getLocation().add(-1, 0, (halfWidth - 2));
                    circleEnd = actBlock.getLocation().add(-1 * (fullWidth - 2), fullWidth - 3, -1 * (halfWidth - 2));
                    alchemyCheck(fromType, fromData, toType, toData, circleStart, circleEnd, startLoc, endLoc, player, fullWidth - 2);
                    lightning = true;
                }
                // Storage circle
                else {
                    if (fullWidth - 2 > storageCircleSize) {
                        return;
                    }
                    startLoc = actBlock.getLocation().add(-1, 0, (halfWidth - 2));
                    endLoc = actBlock.getLocation().add((-1 * (fullWidth - 2)), (fullWidth - 3), (-1 * (halfWidth - 2)));
                    GeometricMagicStorageCircle.storageCircle(startLoc, endLoc, player, (fullWidth - 2));
                    lightning = true;
                }
            }
        } else if (actBlock.getRelative(1, 0, 0).getType() == Material.REDSTONE_WIRE && actBlock.getRelative(-1, 0, 0).getType() == Material.REDSTONE_WIRE) {
            halfWidth = 0;
            while (actBlock.getRelative(halfWidth, 0, 0).getType() == Material.REDSTONE_WIRE) {
                halfWidth++;
            }
            fullWidth = (halfWidth * 2) - 1;
            // System.out
            // .println("half is " + halfWidth + " full is " + fullWidth);
            int dimensionOfEffect = (fullWidth - 2) * (fullWidth - 2);
            if (actBlock.getRelative(0, 0, -1 * (fullWidth - 1)).getType() == Material.REDSTONE_WIRE) {
                // north
                // System.out.println("transmutationCircle north");
                if (actBlock.getRelative(halfWidth, 0, (-1 * (halfWidth - 1))).getType() == Material.REDSTONE_WIRE
                        && actBlock.getRelative((-1 * halfWidth), 0, (-1 * (halfWidth - 1))).getType() == Material.REDSTONE_WIRE) {
                    if (fullWidth - 2 > transmutationCircleSize) {
                        return;
                    }
                    Block fromBlock = actBlock.getLocation().add(-1 * (halfWidth + 1), 0, -1 * (halfWidth - 1)).getBlock();
                    Block toBlock = actBlock.getLocation().add(halfWidth + 1, 0, -1 * (halfWidth - 1)).getBlock();
                    fromType = fromBlock.getType();
                    toType = toBlock.getType();
                    byte fromData = fromBlock.getData();
                    byte toData = toBlock.getData();
                    startLoc = actBlock.getLocation().add(-1 * dimensionOfEffect / 2, 0, -1 * fullWidth);
                    endLoc = actBlock.getLocation().add(dimensionOfEffect / 2 - 1, dimensionOfEffect - 1, -1 * (dimensionOfEffect + fullWidth) + 1);
                    circleStart = actBlock.getLocation().add(-1 * (halfWidth - 2), 0, -1);
                    circleEnd = actBlock.getLocation().add((halfWidth - 2), fullWidth - 3, -1 * (fullWidth - 2));
                    alchemyCheck(fromType, fromData, toType, toData, circleStart, circleEnd, startLoc, endLoc, player, fullWidth - 2);
                    lightning = true;
                }
                // Storage circle
                else {
                    if (fullWidth - 2 > storageCircleSize) {
                        return;
                    }
                    startLoc = actBlock.getLocation().add((halfWidth - 2), 0, -1);
                    endLoc = actBlock.getLocation().add((halfWidth - 2), (fullWidth - 3), (-1 * (fullWidth - 2)));
                    GeometricMagicStorageCircle.storageCircle(startLoc, endLoc, player, (fullWidth - 2));
                    lightning = true;
                }
            } else if (actBlock.getRelative(0, 0, (fullWidth - 1)).getType() == Material.REDSTONE_WIRE) {
                // south
                // System.out.println("transmutationCircle south");
                if (actBlock.getRelative(halfWidth, 0, (halfWidth - 1)).getType() == Material.REDSTONE_WIRE
                        && actBlock.getRelative((-1 * halfWidth), 0, (halfWidth - 1)).getType() == Material.REDSTONE_WIRE) {
                    if (fullWidth - 2 > transmutationCircleSize) {
                        return;
                    }
                    Block fromBlock = actBlock.getLocation().add(halfWidth + 1, 0, halfWidth - 1).getBlock();
                    Block toBlock = actBlock.getLocation().add(-1 * (halfWidth + 1), 0, halfWidth - 1).getBlock();
                    fromType = fromBlock.getType();
                    toType = toBlock.getType();
                    byte fromData = fromBlock.getData();
                    byte toData = toBlock.getData();
                    startLoc = actBlock.getLocation().add(dimensionOfEffect / 2, 0, fullWidth);
                    endLoc = actBlock.getLocation().add(-1 * dimensionOfEffect / 2 + 1, dimensionOfEffect - 1, fullWidth + dimensionOfEffect - 1);
                    circleStart = actBlock.getLocation().add(halfWidth - 2, 0, 1);
                    circleEnd = actBlock.getLocation().add(-1 * (halfWidth - 2), fullWidth - 3, (fullWidth - 2));
                    alchemyCheck(fromType, fromData, toType, toData, circleStart, circleEnd, startLoc, endLoc, player, fullWidth - 2);
                    lightning = true;
                }
                // Storage circle
                else {
                    if (fullWidth - 2 > storageCircleSize) {
                        return;
                    }
                    startLoc = actBlock.getLocation().add((halfWidth - 2), 0, 1);
                    endLoc = actBlock.getLocation().add((-1 * (halfWidth - 2)), (fullWidth - 3), (fullWidth - 2));
                    GeometricMagicStorageCircle.storageCircle(startLoc, endLoc, player, (fullWidth - 2));
                    lightning = true;
                }
            }
        }
        if (lightning)
            actBlock.getWorld().strikeLightningEffect(actBlock.getLocation());
    }

    public static void alchemyCheck(Material a, byte fromData, Material b, byte toData, Location circleStart, Location circleEnd, Location start, Location end, Player player, int width) {
        Block startBlock = circleStart.getBlock();
        int xIteration = 0;
        int yIteration = 0;
        int zIteration = 0;
        if (circleStart.getX() < circleEnd.getX()) {
            if (circleStart.getZ() < circleEnd.getZ()) {
                // east
                // System.out.println("alchemyCheck east");
                while (startBlock.getY() <= circleEnd.getY()) {
                    while (startBlock.getX() <= circleEnd.getX()) {
                        while (startBlock.getZ() <= circleEnd.getZ()) {
                            if (startBlock.getType() != Material.AIR) {
                                GeometricMagicPlayerListener.alchemyFiller(a, fromData, b, toData, start.getBlock().getRelative(xIteration * width, yIteration * width, zIteration * width).getLocation(),
                                        start.getBlock().getRelative(xIteration * width + width - 1, yIteration * width + width - 1, (zIteration * width + (width - 1))).getLocation(), player, true);
                            }
                            zIteration++;
                            startBlock = startBlock.getRelative(0, 0, 1);
                        }
                        xIteration++;
                        startBlock = circleStart.getBlock().getRelative(xIteration, yIteration, 0);
                        zIteration = 0;
                    }
                    yIteration++;
                    xIteration = 0;
                    startBlock = circleStart.getBlock().getRelative(0, yIteration, 0);
                }
            } else {
                // north
                // System.out.println("alchemyCheck north");
                while (startBlock.getY() <= circleEnd.getY()) {
                    while (startBlock.getZ() >= circleEnd.getZ()) {
                        while (startBlock.getX() <= circleEnd.getX()) {
                            if (startBlock.getType() != Material.AIR) {
                                GeometricMagicPlayerListener.alchemyFiller(a, fromData, b, toData, start.getBlock().getRelative(xIteration * width, yIteration * width, zIteration * width).getLocation(),
                                        start.getBlock().getRelative(xIteration * width + width - 1, yIteration * width + width - 1, (zIteration * width - (width - 1))).getLocation(), player, true);
                            }
                            xIteration++;
                            // System.out.println("xloop " + xIteration);
                            startBlock = startBlock.getRelative(1, 0, 0);
                        }
                        zIteration--;
                        // System.out.println("zloop " + zIteration);
                        startBlock = circleStart.getBlock().getRelative(0, yIteration, zIteration);
                        xIteration = 0;
                    }
                    yIteration++;
                    // System.out.println("yloop " + yIteration);
                    zIteration = 0;
                    startBlock = circleStart.getBlock().getRelative(0, yIteration, 0);
                }
            }
        } else {
            if (circleStart.getZ() > circleEnd.getZ()) {
                // west
                // System.out.println("alchemyCheck west");
                while (startBlock.getY() <= circleEnd.getY()) {
                    while (startBlock.getX() >= circleEnd.getX()) {
                        while (startBlock.getZ() >= circleEnd.getZ()) {
                            if (startBlock.getType() != Material.AIR) {
                                GeometricMagicPlayerListener.alchemyFiller(a, fromData, b, toData, start.getBlock().getRelative(xIteration * width, yIteration * width, zIteration * width).getLocation(),
                                        start.getBlock().getRelative(xIteration * width - (width - 1), yIteration * width + width - 1, (zIteration * width - (width - 1))).getLocation(), player, true);
                            }
                            zIteration--;
                            startBlock = startBlock.getRelative(0, 0, -1);
                        }
                        xIteration--;
                        startBlock = circleStart.getBlock().getRelative(xIteration, yIteration, 0);
                        zIteration = 0;
                    }
                    yIteration++;
                    xIteration = 0;
                    startBlock = circleStart.getBlock().getRelative(0, yIteration, 0);
                }
            } else {
                // south
                // System.out.println("alchemyCheck south");
                while (startBlock.getY() <= circleEnd.getY()) {
                    while (startBlock.getZ() <= circleEnd.getZ()) {
                        while (startBlock.getX() >= circleEnd.getX()) {
                            if (startBlock.getType() != Material.AIR) {
                                GeometricMagicPlayerListener.alchemyFiller(a, fromData, b, toData, start.getBlock().getRelative(xIteration * width, yIteration * width, zIteration * width).getLocation(),
                                        start.getBlock().getRelative(xIteration * width - (width - 1), yIteration * width + width - 1, (zIteration * width + (width - 1))).getLocation(), player, true);
                            }
                            xIteration--;
                            // System.out.println("xloop");
                            startBlock = startBlock.getRelative(-1, 0, 0);
                        }
                        zIteration++;
                        // System.out.println("zloop");
                        startBlock = circleStart.getBlock().getRelative(0, yIteration, zIteration);
                        xIteration = 0;
                    }
                    yIteration++;
                    // System.out.println("yloop");
                    zIteration = 0;
                    startBlock = circleStart.getBlock().getRelative(0, yIteration, 0);
                }
            }
        }
        return;
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
            } else {
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
        } else {
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
            } else {
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
                double pay = GeometricMagic.calculatePay(a, fromData, b, toData, player);

                // exempt player from AntiCheat check
                if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                    AnticheatAPI.exemptPlayer(player, CheckType.FAST_PLACE);
                    AnticheatAPI.exemptPlayer(player, CheckType.FAST_BREAK);
                    AnticheatAPI.exemptPlayer(player, CheckType.LONG_REACH);
                    AnticheatAPI.exemptPlayer(player, CheckType.NO_SWING);
                }

                if (player != null) {
                    if (startBlock.getType() == a && startBlock.getData() == fromData) {

                        if (-1 * GeometricMagic.getBalance(player) < pay || !charge) {

                            // Block break
                            if (a != Material.AIR && b == Material.AIR) {

                                if (!GeometricMagic.checkBreakBlacklist(a.getId())) {

                                    Location blockLocation = startBlock.getLocation();

                                    if (GeometricMagic.checkBlockBreakSimulation(blockLocation, player)) {
                                        // Change block
                                        startBlock.setType(b);
                                        if (toData != 0)
                                            startBlock.setData(toData);

                                        if (charge) {
                                            if (GeometricMagic.getTransmutationCostSystem(plugin).equalsIgnoreCase("vault")) {

                                                Economy econ = GeometricMagic.getEconomy();

                                                // Deposit or withdraw to players Vault account
                                                if (pay > 0) {
                                                    econ.depositPlayer(player.getName(), pay);
                                                } else if (pay < 0) {
                                                    econ.withdrawPlayer(player.getName(), pay * -1);
                                                }
                                            } else if (GeometricMagic.getTransmutationCostSystem(plugin).equalsIgnoreCase("xp")) {
                                                player.setLevel((int) (player.getLevel() + pay));
                                            }
                                        }
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                    return;
                                }
                            }

                            // Block place
                            else if (a == Material.AIR && b != Material.AIR) {

                                if (!GeometricMagic.checkPlaceBlacklist(b.getId())) {

                                    Location blockLocation = startBlock.getLocation();
                                    int blockID = b.getId();
                                    byte blockData = toData;

                                    if (GeometricMagic.checkBlockPlaceSimulation(blockLocation, blockID, blockData, blockLocation, player)) {
                                        // Change block
                                        startBlock.setType(b);
                                        if (toData != 0)
                                            startBlock.setData(toData);

                                        if (charge) {
                                            if (GeometricMagic.getTransmutationCostSystem(plugin).equalsIgnoreCase("vault")) {

                                                Economy econ = GeometricMagic.getEconomy();

                                                // Deposit or withdraw to players Vault account
                                                if (pay > 0) {
                                                    econ.depositPlayer(player.getName(), pay);
                                                } else if (pay < 0) {
                                                    econ.withdrawPlayer(player.getName(), pay * -1);
                                                }
                                            } else if (GeometricMagic.getTransmutationCostSystem(plugin).equalsIgnoreCase("xp")) {
                                                player.setLevel((int) (player.getLevel() + pay));
                                            }
                                        }
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "[GeometricMagic] That block is blacklisted");
                                    return;
                                }
                            }

                            // Block break and place
                            else if (a != Material.AIR && b != Material.AIR) {

                                if (!GeometricMagic.checkBreakBlacklist(a.getId()) && !GeometricMagic.checkPlaceBlacklist(b.getId())) {

                                    Location blockLocation = startBlock.getLocation();
                                    int blockID = b.getId();
                                    byte blockData = toData;

                                    if (GeometricMagic.checkBlockBreakSimulation(blockLocation, player)
                                            && GeometricMagic.checkBlockPlaceSimulation(blockLocation, blockID, blockData, blockLocation, player)) {
                                        // Change block
                                        startBlock.setType(b);
                                        if (toData != 0)
                                            startBlock.setData(toData);

                                        if (charge) {
                                            if (GeometricMagic.getTransmutationCostSystem(plugin).equalsIgnoreCase("vault")) {

                                                Economy econ = GeometricMagic.getEconomy();

                                                // Deposit or withdraw to players Vault account
                                                if (pay > 0) {
                                                    econ.depositPlayer(player.getName(), pay);
                                                } else if (pay < 0) {
                                                    econ.withdrawPlayer(player.getName(), pay * -1);
                                                }
                                            } else if (GeometricMagic.getTransmutationCostSystem(plugin).equalsIgnoreCase("xp")) {
                                                player.setLevel((int) (player.getLevel() + pay));
                                            }
                                        }
                                    }
                                } else {
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

        double pay = GeometricMagic.calculatePay(
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
                    if (pointer.getBlock().getType() == fromData.getItemType() && (-1 * GeometricMagic.getBalance(player) < pay)) {
                        GeometricMagic.playerTransaction(player, pay);
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