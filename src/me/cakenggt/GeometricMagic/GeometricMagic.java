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
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GeometricMagic extends JavaPlugin {
    private static Economy economy;
    // autoUpdater isn't used
    //public boolean autoUpdateNotify;
    public boolean upToDate = true;
    File configFile;
    private Listener playerListener;
    private Listener entityListener;

    public static boolean checkSacrifices(Player player) throws IOException {
        File myFile = new File("plugins/GeometricMagic/sacrifices.txt");
        if (!myFile.exists()) {
            return false;
        }
        Scanner inputFile = new Scanner(myFile);
        while (inputFile.hasNextLine()) {
            String name = inputFile.nextLine();
            if (name.equals(player.getName())) {
                // close this before we return
                inputFile.close();
                return true;
            }
            inputFile.nextLine();
        }
        inputFile.close();
        return false;
        // playername
        // [1, 1, 1, 2]
    }

    public static boolean checkSacrificed(Player player) throws IOException {
        File myFile = new File("plugins/GeometricMagic/sacrificed.txt");
        if (!myFile.exists()) {
            return false;
        }
        Scanner inputFile = new Scanner(myFile);
        while (inputFile.hasNextLine()) {
            String name = inputFile.nextLine();
            if (name.equals(player.getName())) {
                // close this before we return
                inputFile.close();
                return true;
            }
        }
        inputFile.close();
        return false;
        // playername
    }

    // Vault Support
    public static Economy getEconomy() {
        return economy;
    }

    public static void isCircle(Player player, World world, Block actBlock) throws IOException {
        // System.out.println("isCircle?");
        if (actBlock.getType() == Material.REDSTONE_WIRE && player.getItemInHand().getAmount() == 0) {
            // System.out.println("isCircle");
            circleChooser(player, world, actBlock);
        }
        if (player.getItemInHand().getType() == Material.FLINT) {

            // set circle cool down
            if (!player.hasPermission("geometricmagic.bypass.cooldown")) {
                int coolDown = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.cooldown");
                if (GeometricMagicPlayerListener.mapCoolDowns.containsKey(player.getName() + " set circle")) {
                    long diff = (System.currentTimeMillis() - GeometricMagicPlayerListener.mapCoolDowns.get(player.getName() + " set circle")) / 1000;
                    if (diff < coolDown) {
                        // still cooling down
                        player.sendMessage(coolDown - diff + " seconds before you can do that again.");
                        return;
                    }
                }
                GeometricMagicPlayerListener.mapCoolDowns.put(player.getName() + " set circle", System.currentTimeMillis());
            }

            File myFile = new File("plugins/GeometricMagic/sacrifices.txt");
            String circle = "[0, 0, 0, 0]";
            if (myFile.exists()) {
                Scanner inputFile = new Scanner(myFile);
                while (inputFile.hasNextLine()) {
                    String name = inputFile.nextLine();
                    if (name.equals(player.getName())) {
                        circle = inputFile.nextLine();
                    } else
                        inputFile.nextLine();
                }
                inputFile.close();
            } else {
                return;
            }

            try {
                // exempt player from AntiCheat check
                if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                    AnticheatAPI.exemptPlayer(player, CheckType.FAST_PLACE);
                    AnticheatAPI.exemptPlayer(player, CheckType.FAST_BREAK);
                    AnticheatAPI.exemptPlayer(player, CheckType.LONG_REACH);
                    AnticheatAPI.exemptPlayer(player, CheckType.NO_SWING);
                }

                if (circle.equals("0")) {
                    circleChooser(player, world, actBlock);
                } else
                    GeometricMagicSetCircles.setCircleEffects(player, player.getWorld(), player.getLocation().getBlock(), actBlock, circle);

                // unexempt player from AntiCheat check
                if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                    AnticheatAPI.unexemptPlayer(player, CheckType.FAST_PLACE);
                    AnticheatAPI.unexemptPlayer(player, CheckType.FAST_BREAK);
                    AnticheatAPI.unexemptPlayer(player, CheckType.LONG_REACH);
                    AnticheatAPI.unexemptPlayer(player, CheckType.NO_SWING);
                }
            } catch (IOException e1) {
                e1.printStackTrace();

                // unexempt player from AntiCheat check
                if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                    AnticheatAPI.unexemptPlayer(player, CheckType.FAST_PLACE);
                    AnticheatAPI.unexemptPlayer(player, CheckType.FAST_BREAK);
                    AnticheatAPI.unexemptPlayer(player, CheckType.LONG_REACH);
                    AnticheatAPI.unexemptPlayer(player, CheckType.NO_SWING);
                }
            }
        }
    }

    // Method to take nearby world entities, and return all valid Item instances in a list
    public static List<Entity> nearbyItems(Block originBlock, int radius) {
        List<Entity> worldEntities = originBlock.getLocation().getWorld().getEntities();
        List<Entity> itemsList = new ArrayList<Entity>();
        for (Entity item : worldEntities) {
            if (item.getLocation().distance(originBlock.getLocation()) <= radius && item instanceof Item) {
                itemsList.add(item);
            }
        }
        return itemsList;
    }

    public static List<Entity> nearbyItems(Block originBlock) {
        int defaultRadius = 2;
        return nearbyItems(originBlock, defaultRadius);
    }

    public static double getBalance(Player player) {

        if (getTransmutationCostSystem(GeometricMagicPlayerListener.plugin).equalsIgnoreCase("vault")) {
            Economy econ = getEconomy();

            return econ.getBalance(player.getName());
        } else if (getTransmutationCostSystem(GeometricMagicPlayerListener.plugin).equalsIgnoreCase("xp")) {

            return (double) player.getLevel();
        }
        return 0;
    }

    public static double calculatePay(Material a, byte fromData, Material b, byte toData, Player player) {
        double pay = (getBlockValue(GeometricMagicPlayerListener.plugin, a.getId(), (int) fromData) - getBlockValue(GeometricMagicPlayerListener.plugin, b.getId(), (int) toData));

        // Apply Philosopher's Stone to transmutes config variable
        String stoneConfig = GeometricMagicPlayerListener.plugin.getConfig().getString("transmutation.stone");
        if (stoneConfig == "true") {
            return (double) (pay * philosopherStoneModifier(player));
        } else {
            return pay;
        }
    }

    public static double philosopherStoneModifier(Player player) {
        double modifier = 1;
        int stackCount = 0;
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null && inventory.getItem(i).getType() == Material.PORTAL)
                stackCount += inventory.getItem(i).getAmount();
        }
        float multiplierModifier = (float) GeometricMagicPlayerListener.plugin.getConfig().getDouble("philosopherstone.modifier");

        modifier = 1 / (Math.pow(2, stackCount) * multiplierModifier);
        return modifier;
    }

    public static boolean checkBlockPlaceSimulation(Location target, int typeId, byte data, Location placedAgainst, Player player) {
        Block placedBlock = target.getBlock();
        BlockState replacedBlockState = placedBlock.getState();
        int oldType = replacedBlockState.getTypeId();
        byte oldData = replacedBlockState.getRawData();

        // Set the new state without physics.
        placedBlock.setTypeIdAndData(typeId, data, false);
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(placedBlock, replacedBlockState, placedAgainst.getBlock(), null, player, true);
        getPluginManager().callEvent(placeEvent);

        // Revert to the old state without physics.
        placedBlock.setTypeIdAndData(oldType, oldData, false);
        return !placeEvent.isCancelled();
    }

    public static boolean checkBlockBreakSimulation(Location target, Player player) {
        Block block = target.getBlock();
        BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
        getPluginManager().callEvent(breakEvent);
        return !breakEvent.isCancelled();
    }

    public static boolean checkBreakBlacklist(int m) {
        List<String> blacklist = GeometricMagicPlayerListener.plugin.getConfig().getStringList("blacklist.break");
        for (String s : blacklist) {
            if (s.equals(String.valueOf(m)))
                return true;
        }
        return false;
    }

    public static boolean checkPlaceBlacklist(int m) {
        List<String> blacklist = GeometricMagicPlayerListener.plugin.getConfig().getStringList("blacklist.place");
        for (String s : blacklist) {
            if (s.equals(String.valueOf(m)))
                return true;
        }
        return false;
    }

    public static boolean playerTransaction(Player player, double amount) {
        if (getTransmutationCostSystem(GeometricMagicPlayerListener.plugin).equalsIgnoreCase("vault")) {

            Economy econ = getEconomy();

            // Deposit or withdraw to players Vault account
            if (amount > 0) {
                econ.depositPlayer(player.getName(), amount);
            } else if (amount < 0) {
                econ.withdrawPlayer(player.getName(), amount * -1);
            }
            return true;
        } else if (getTransmutationCostSystem(GeometricMagicPlayerListener.plugin).equalsIgnoreCase("xp")) {
            player.setLevel((int) (player.getLevel() + amount));
            return true;
        }
        return false;
    }

    public static void circleChooser(Player player, World world, Block actBlock) {
        Block northBlock = actBlock.getRelative(0, 0, -1);
        Block southBlock = actBlock.getRelative(0, 0, 1);
        Block eastBlock = actBlock.getRelative(1, 0, 0);
        Block westBlock = actBlock.getRelative(-1, 0, 0);

        // teleportation circle
        if (northBlock.getType() == Material.REDSTONE_WIRE && southBlock.getType() == Material.REDSTONE_WIRE && eastBlock.getType() == Material.REDSTONE_WIRE
                && westBlock.getType() == Material.REDSTONE_WIRE) {
            if (player.hasPermission("geometricmagic.teleportation")) {
                // System.out.println("teleportation");
                GeometricMagicTeleportationCircle.teleportationCircle(player, world, actBlock);
            } else
                player.sendMessage("You do not have permission to use this circle");

            // micro circle
        } else if (northBlock.getType() != Material.REDSTONE_WIRE && southBlock.getType() != Material.REDSTONE_WIRE && eastBlock.getType() != Material.REDSTONE_WIRE
                && westBlock.getType() != Material.REDSTONE_WIRE && actBlock.getRelative(-3, 0, 0).getType() != Material.REDSTONE_WIRE
                && actBlock.getRelative(3, 0, 0).getType() != Material.REDSTONE_WIRE && actBlock.getRelative(0, 0, -3).getType() != Material.REDSTONE_WIRE
                && actBlock.getRelative(0, 0, 3).getType() != Material.REDSTONE_WIRE) {
            if (player.hasPermission("geometricmagic.micro")) {
                // System.out.println("micro");
                GeometricMagicPlayerListener.plugin.getLogger().info(actBlock.getRelative(BlockFace.DOWN).toString());
                GeometricMagicSetCircles.microCircle(player, world, actBlock);
            } else
                player.sendMessage("You do not have permission to use this circle");

            // transmutation circle
        } else if ((northBlock.getType() == Material.REDSTONE_WIRE && southBlock.getType() == Material.REDSTONE_WIRE && eastBlock.getType() != Material.REDSTONE_WIRE && westBlock.getType() != Material.REDSTONE_WIRE)
                || (northBlock.getType() != Material.REDSTONE_WIRE && southBlock.getType() != Material.REDSTONE_WIRE && eastBlock.getType() == Material.REDSTONE_WIRE && westBlock.getType() == Material.REDSTONE_WIRE)) {

            // transmutation circle size permissions
            // - allows use of all circles smaller than then the max
            // size permission node they have
            int transmutationCircleSize = 1;
            if (player.hasPermission("geometricmagic.transmutation.9")) {
                transmutationCircleSize = 9;
            } else if (player.hasPermission("geometricmagic.transmutation.7")) {
                transmutationCircleSize = 7;
            } else if (player.hasPermission("geometricmagic.transmutation.5")) {
                transmutationCircleSize = 5;
            } else if (player.hasPermission("geometricmagic.transmutation.3")) {
                transmutationCircleSize = 3;
            } else if (player.hasPermission("geometricmagic.transmutation.1")) {
                transmutationCircleSize = 1;
            } else {
                transmutationCircleSize = 0;
                player.sendMessage("You do not have permission to use this circle");
            }

            // Storage circle size permissions
            int storageCircleSize = 1;
            if (player.hasPermission("geometricmagic.storage.9")) {
                storageCircleSize = 9;
            } else if (player.hasPermission("geometricmagic.storage.7")) {
                storageCircleSize = 7;
            } else if (player.hasPermission("geometricmagic.storage.5")) {
                storageCircleSize = 5;
            } else if (player.hasPermission("geometricmagic.storage.3")) {
                storageCircleSize = 3;
            } else if (player.hasPermission("geometricmagic.storage.1")) {
                storageCircleSize = 1;
            } else {
                storageCircleSize = 0;
                player.sendMessage("You do not have permission to use this circle");
            }

            int circleSize = (transmutationCircleSize > storageCircleSize) ? transmutationCircleSize : storageCircleSize;

            // System.out.println("circleSize:" + circleSize);

            // transmute cool down
            if (!player.hasPermission("geometricmagic.bypass.cooldown")) {
                int coolDown = GeometricMagicPlayerListener.plugin.getConfig().getInt("transmutation.cooldown");
                if (GeometricMagicPlayerListener.mapCoolDowns.containsKey(player.getName() + " transmute circle")) {
                    long diff = (System.currentTimeMillis() - GeometricMagicPlayerListener.mapCoolDowns.get(player.getName() + " transmute circle")) / 1000;
                    if (diff < coolDown) {
                        // still cooling down
                        player.sendMessage(coolDown - diff + " seconds before you can do that again.");
                        return;
                    }
                }
                GeometricMagicPlayerListener.mapCoolDowns.put(player.getName() + " transmute circle", System.currentTimeMillis());
            }

            if (circleSize > 0) {
                GeometricMagicTransmutationThread.transmutationCircle(player, world, actBlock, transmutationCircleSize, storageCircleSize);
            }

            // set circle
        } else if (northBlock.getType() != Material.REDSTONE_WIRE && southBlock.getType() != Material.REDSTONE_WIRE && eastBlock.getType() != Material.REDSTONE_WIRE
                && westBlock.getType() != Material.REDSTONE_WIRE && actBlock.getRelative(-3, 0, 0).getType() == Material.REDSTONE_WIRE
                && actBlock.getRelative(3, 0, 0).getType() == Material.REDSTONE_WIRE && actBlock.getRelative(0, 0, -3).getType() == Material.REDSTONE_WIRE
                && actBlock.getRelative(0, 0, 3).getType() == Material.REDSTONE_WIRE) {

            if (player.hasPermission("geometricmagic.set")) {
                // set circle cool down
                if (!player.hasPermission("geometricmagic.bypass.cooldown")) {
                    int coolDown = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.cooldown");
                    if (GeometricMagicPlayerListener.mapCoolDowns.containsKey(player.getName() + " set circle")) {
                        long diff = (System.currentTimeMillis() - GeometricMagicPlayerListener.mapCoolDowns.get(player.getName() + " set circle")) / 1000;
                        if (diff < coolDown) {
                            // still cooling down
                            player.sendMessage(coolDown - diff + " seconds before you can do that again.");
                            return;
                        }
                    }
                    GeometricMagicPlayerListener.mapCoolDowns.put(player.getName() + " set circle", System.currentTimeMillis());
                }

                // exempt player from AntiCheat check
                if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                    AnticheatAPI.exemptPlayer(player, CheckType.FAST_PLACE);
                    AnticheatAPI.exemptPlayer(player, CheckType.FAST_BREAK);
                    AnticheatAPI.exemptPlayer(player, CheckType.LONG_REACH);
                    AnticheatAPI.exemptPlayer(player, CheckType.NO_SWING);
                }

                GeometricMagicSetCircles.setCircleRemote(player, world, actBlock);

                // unexempt player from AntiCheat check
                if (Bukkit.getServer().getPluginManager().getPlugin("AntiCheat") != null) {
                    AnticheatAPI.unexemptPlayer(player, CheckType.FAST_PLACE);
                    AnticheatAPI.unexemptPlayer(player, CheckType.FAST_BREAK);
                    AnticheatAPI.unexemptPlayer(player, CheckType.LONG_REACH);
                    AnticheatAPI.unexemptPlayer(player, CheckType.NO_SWING);
                }
            } else
                player.sendMessage("You do not have permission to use this circle");

            // no circle
        } else {
            return;
        }
    }

    public static String getTransmutationCostSystem(GeometricMagic plugin) {
        return plugin.getConfig().getString("transmutation.cost").toString();
    }

    public static Integer getBlockValue(GeometricMagic plugin, int ID, int Data) {
        return plugin.getConfig().getInt("values." + ID + "." + Data);
    }

    public static PluginManager getPluginManager() {
        return GeometricMagicPlayerListener.plugin.getServer().getPluginManager();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        // If the player typed /setcircle then do the following...
        if (cmd.getName().equalsIgnoreCase("setcircle")) {
            Player player = null;

            if (sender instanceof Player) {
                player = (Player) sender;

                if (!player.hasPermission("geometricmagic.command.setcircle")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return true;
                }

                boolean sacrificed = false;

                if (!player.hasPermission("geometricmagic.bypass.sacrifice")) {
                    try {
                        sacrificed = checkSacrificed(player);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                if (sacrificed) {
                    player.sendMessage("You have sacrificed your alchemy abilities forever.");
                    return true;
                }

                boolean sacrifices = false;
                try {
                    sacrifices = checkSacrifices(player);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                // check if player has flint and is using the proper arguments
                boolean hasFlint = player.getInventory().contains(Material.FLINT);
                if ((args.length != 1 || (args[0].length() != 4 && args[0].length() != 1)) && hasFlint && sacrifices) {
                    sender.sendMessage(ChatColor.RED + cmd.getUsage());
                    return true;
                } else if (args.length == 0 && !hasFlint && sacrifices) {
                    // they don't have flint so give them one
                    ItemStack oneFlint = new ItemStack(Material.FLINT, 1);
                    player.getWorld().dropItem(player.getLocation(), oneFlint);
                    return true;
                } else if (!sacrifices) {
                    player.sendMessage(ChatColor.RED + "You must perform a human sacrifice if you wish to use this command.");
                    return true;
                }

                if (args[0].length() == 1 && args[0].equalsIgnoreCase("0")) {
                    sender.sendMessage("Casting circles on right click now disabled, set right click to a viable circle to enable");
                    String inputString = args[0];
                    try {
                        sacrificeCircle(sender, inputString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;

                } else {
                    String inputString = "[" + args[0].charAt(0) + ", " + args[0].charAt(1) + ", " + args[0].charAt(2) + ", " + args[0].charAt(3) + "]";
                    try {
                        sacrificeCircle(sender, inputString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }

            if (player == null) {
                sender.sendMessage("This command can only be run by a player");
                return false;
            }
        } else if (cmd.getName().equalsIgnoreCase("circles")) {
            if (sender.hasPermission("geometricmagic.command.circles")) {
                sender.sendMessage(ChatColor.GREEN + "1133" + ChatColor.RESET + " Repair Circle");
                sender.sendMessage(ChatColor.GREEN + "1222" + ChatColor.RESET + " Conversion Circle");
                sender.sendMessage(ChatColor.GREEN + "1233" + ChatColor.RESET + " Philosopher's Stone Circle");
                sender.sendMessage(ChatColor.GREEN + "1234" + ChatColor.RESET + " Boron Circle");
                sender.sendMessage(ChatColor.GREEN + "2223" + ChatColor.RESET + " Soul Circle");
                sender.sendMessage(ChatColor.GREEN + "2224" + ChatColor.RESET + " Homunculus Circle");
                sender.sendMessage(ChatColor.GREEN + "2244" + ChatColor.RESET + " Safe Teleportation Circle");
                sender.sendMessage(ChatColor.GREEN + "2333" + ChatColor.RESET + " Explosion Circle");
                sender.sendMessage(ChatColor.GREEN + "3334" + ChatColor.RESET + " Fire Circle");
                sender.sendMessage(ChatColor.GREEN + "3344" + ChatColor.RESET + " Fire Explosion Circle");
                sender.sendMessage(ChatColor.GREEN + "3444" + ChatColor.RESET + " Human Transmutation Circle");
                sender.sendMessage(ChatColor.GREEN + "0111" + ChatColor.RESET + " Bed Circle");
                sender.sendMessage(ChatColor.GREEN + "0044" + ChatColor.RESET + " Pig Circle");
                sender.sendMessage(ChatColor.GREEN + "0144" + ChatColor.RESET + " Sheep Circle");
                sender.sendMessage(ChatColor.GREEN + "0244" + ChatColor.RESET + " Cow Circle");
                sender.sendMessage(ChatColor.GREEN + "0344" + ChatColor.RESET + " Chicken Circle");
            } else
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("geometricmagic")) {
            if (sender.hasPermission("geometricmagic.command.geometricmagic")) {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.GREEN + "*********** GeometricMagic Help ***********");
                    sender.sendMessage(ChatColor.GREEN + "*" + ChatColor.GRAY + "************* User Commands *************");
                    sender.sendMessage(ChatColor.GREEN + "*" + ChatColor.GRAY + "*" + ChatColor.YELLOW + " /geometricmagic" + ChatColor.WHITE + " - Display this help dialogue");
                    sender.sendMessage(ChatColor.GREEN + "*" + ChatColor.GRAY + "*" + ChatColor.YELLOW + " /setcircle <####>" + ChatColor.WHITE + " - Bind set circle #### to flint. 0 resets");
                    sender.sendMessage(ChatColor.GREEN + "*" + ChatColor.GRAY + "*" + ChatColor.YELLOW + " /circles" + ChatColor.WHITE + " - List all set circles");
                    sender.sendMessage(ChatColor.GREEN + "*" + ChatColor.GRAY + "*****************************************");
                    if (sender.hasPermission("geometricmagic.command.geometricmagic.reload")) {
                        sender.sendMessage(ChatColor.GREEN + "*" + ChatColor.RED + "************* Admin Commands ************");
                        sender.sendMessage(ChatColor.GREEN + "*" + ChatColor.RED + "*" + ChatColor.YELLOW + " /geometricmagic reload" + ChatColor.WHITE + " - Reload config file");
                        sender.sendMessage(ChatColor.GREEN + "*" + ChatColor.RED + "*****************************************");
                    }
                    sender.sendMessage(ChatColor.GREEN + "******************************************");
                    return true;
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        if (sender.hasPermission("geometricmagic.command.geometricmagic.reload")) {
                            reloadConfig();
                            sender.sendMessage(ChatColor.GREEN + "Config reload successfully!");
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                            return true;
                        }
                    } else
                        return false;
                } else if (args.length > 1)
                    return false;
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }
        }
        // If this has happened the function will break and return true. if this
        // hasn't happened the a value of false will be returned.
        return false;
    }

    public void sacrificeCircle(CommandSender sender, String inputString) throws IOException {
        // System.out.println("sacrificeCircle for " + inputString);
        File myFile = new File("plugins/GeometricMagic/sacrifices.txt");
        if (myFile.exists()) {
            Scanner inputFileCheck = new Scanner(myFile);
            int j = 0;
            while (inputFileCheck.hasNext()) {
                inputFileCheck.nextLine();
                j++;
            }
            int size = (j + 1) / 2;
            // System.out.println("size of sacrifices file " + size);
            String[] nameArray = new String[size];
            String[] circleArray = new String[size];
            inputFileCheck.close();
            // System.out.println("inputFileCheck closed");
            Scanner inputFile = new Scanner(myFile);
            // System.out.println("inputFile opened");
            for (int i = 0; i < size; i++) {
                nameArray[i] = inputFile.nextLine();
                circleArray[i] = inputFile.nextLine();
            }
            // System.out.println("nameArray[0] is " + nameArray[0]);
            // System.out.println("circleArray[0] is " + circleArray[0]);
            for (int i = 0; i < size; i++) {
                if (nameArray[i].equalsIgnoreCase(sender.getName())) {
                    circleArray[i] = inputString;
                    sender.sendMessage("set-circle " + inputString + " added successfully!");
                }
            }
            // System.out.println("nameArray[0] is " + nameArray[0]);
            // System.out.println("circleArray[0] is " + circleArray[0]);
            inputFile.close();
            PrintWriter outputFile = new PrintWriter("plugins/GeometricMagic/sacrifices.txt");
            for (int i = 0; i < size; i++) {
                outputFile.println(nameArray[i]);
                outputFile.println(circleArray[i]);
            }
            outputFile.close();
        } else {
            return;
        }
    }

    @Override
    public void onDisable() {
        System.out.println(this + " is now disabled!");
    }

    @Override
    public void onEnable() {

        configFile = new File(getDataFolder(), "config.yml");

        // Copy default config file if it doesn't exist
        if (!configFile.exists()) {
            saveDefaultConfig();
            System.out.println("[GeometricMagic] Config file generated!");
        } else {
            try {
                GeometricMagicConfigUpdater.updateConfig(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Transmutation mode: Vault
        if (getConfig().getString("transmutation.cost").toString().equalsIgnoreCase("vault")) {
            // Vault Support
            if (!setupEconomy()) {
                System.out.println("[GeometricMagic] ERROR: You have your transmutation system set to Vault, and yet you don't have Vault. Disabling plugin!");
                getServer().getPluginManager().disablePlugin(this);
            } else {
                System.out.println("[GeometricMagic] Transmutation cost system set to Vault");

                // Register events
                playerListener = new GeometricMagicPlayerListener(this);
                entityListener = new GeometricMagicDamageListener(this);
                getServer().getPluginManager().registerEvents(playerListener, this);
                getServer().getPluginManager().registerEvents(entityListener, this);
                ShapelessRecipe portalRecipe = new ShapelessRecipe(new ItemStack(Material.FIRE, 64)).addIngredient(Material.PORTAL);
                getServer().addRecipe(portalRecipe);
                System.out.println(this + " is now enabled!");
            }
        }
        // Transmutation mode: XP
        else if (getConfig().getString("transmutation.cost").toString().equalsIgnoreCase("xp")) {
            System.out.println("[GeometricMagic] Transmutation cost system set to XP");

            // Register events
            playerListener = new GeometricMagicPlayerListener(this);
            entityListener = new GeometricMagicDamageListener(this);
            getServer().getPluginManager().registerEvents(playerListener, this);
            getServer().getPluginManager().registerEvents(entityListener, this);
            ShapelessRecipe portalRecipe = new ShapelessRecipe(new ItemStack(Material.FIRE, 64)).addIngredient(Material.PORTAL);
            getServer().addRecipe(portalRecipe);
            System.out.println(this + " is now enabled!");
        }
        // Transmutation mode: Unknown
        else {
            System.out.println("[GeometricMagic] ERROR: You have your transmutation cost system set to an unknown value. Disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
        }

        // Plugin metrics
        startPluginMetrics();

        // Get plugin version for auto-update
        int pluginVersion = Integer.parseInt(this.getDescription().getVersion().replace(".", ""));

        // Start auto-update if applicable
        if (getConfig().getBoolean("autoUpdate")) {
            Updater.UpdateType updateType = null;
            if (getConfig().getString("updateType").toLowerCase() == "default") {
                updateType = Updater.UpdateType.DEFAULT;
            } else if (getConfig().getString("updateType").toLowerCase() == "no_download") {
                updateType = Updater.UpdateType.NO_DOWNLOAD;
            } else if (getConfig().getString("updateType").toLowerCase() == "no_version_check") {
                updateType = Updater.UpdateType.NO_VERSION_CHECK;
            } else {
                updateType = Updater.UpdateType.NO_DOWNLOAD;
            }
            Updater updater = new Updater(this, 40378, this.getFile(), updateType, false);
        }
    }

    // Vault Support
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    private void startPluginMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().warning("Failed to start Metrics");
            // Failed to submit the stats :-(
        }
    }
}