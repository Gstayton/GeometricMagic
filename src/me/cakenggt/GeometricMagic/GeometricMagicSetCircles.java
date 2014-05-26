package me.cakenggt.GeometricMagic;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kosan
 * Date: 5/25/14
 * Time: 10:41 PM
 */
public class GeometricMagicSetCircles {
    public static void microCircle(Player player, World world, Block actBlock) {
        if (GeometricMagic.getTransmutationCostSystem(GeometricMagicPlayerListener.plugin).equalsIgnoreCase("vault")) {
            Economy econ = GeometricMagic.getEconomy();

            // Tell the player how much money they have
            player.sendMessage("You have " + econ.format(GeometricMagic.getBalance(player)));
        } else if (GeometricMagic.getTransmutationCostSystem(GeometricMagicPlayerListener.plugin).equalsIgnoreCase("xp")) {
            // Tell the player how many levels they have
            player.sendMessage("Your experience level is " + player.getLevel());
        }

        // Tell player when they can use a set circle
        int coolDown = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.cooldown");
        if (GeometricMagicPlayerListener.mapCoolDowns.containsKey(player.getName() + " set circle")) {
            long diff = (System.currentTimeMillis() - GeometricMagicPlayerListener.mapCoolDowns.get(player.getName() + " set circle")) / 1000;
            if (diff < coolDown) {
                // still cooling down
                player.sendMessage(coolDown - diff + " seconds before you can use a set circle.");
            } else {
                // off cooldown
                player.sendMessage("Your set circle is ready to use.");
            }
        } else {
            // off cooldown
            player.sendMessage("Your set circle is ready to use.");
        }

        // Tell player when they can use a transmute circle
        coolDown = GeometricMagicPlayerListener.plugin.getConfig().getInt("transmutation.cooldown");
        if (GeometricMagicPlayerListener.mapCoolDowns.containsKey(player.getName() + " transmute circle")) {
            long diff = (System.currentTimeMillis() - GeometricMagicPlayerListener.mapCoolDowns.get(player.getName() + " transmute circle")) / 1000;
            if (diff < coolDown) {
                // still cooling down
                player.sendMessage(coolDown - diff + " seconds before you can use a transmutation circle.");
            } else {
                // off cooldown
                player.sendMessage("Your transmutation circle is ready to use.");
            }
        } else {
            // off cooldown
            player.sendMessage("Your transmutation circle is ready to use.");
        }

        List<Entity> entitiesList = player.getNearbyEntities(100, 32, 100);
        int limitCount = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.limitarrows");

        for (int i = 0; i < entitiesList.size() && limitCount != 0; i++) {
            if (entitiesList.get(i) instanceof Arrow) {
                Arrow shotArrow = (Arrow) entitiesList.get(i);
                if (shotArrow.getLocation().getBlock().getType() == Material.REDSTONE_WIRE) {
                    limitCount--;
                    Block newActPoint = shotArrow.getLocation().getBlock();
                    Player newPlayer = (Player) shotArrow.getShooter();
                    GeometricMagic.circleChooser(newPlayer, world, newActPoint);
                }
            }
        }
    }

    public static void setCircleRemote(Player player, World world, Block actBlock) {
        Boolean remote = false;
        Block effectBlock = actBlock;

        List<Entity> entitiesList = player.getNearbyEntities(100, 32, 100);
        int limit = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.limitarrows");
        int limitCount = limit;

        for (int i = 0; i < entitiesList.size() && limitCount != 0; i++) {
            if (entitiesList.get(i) instanceof Arrow) {
                Arrow shotArrow = (Arrow) entitiesList.get(i);
                if (shotArrow.getLocation().getBlock().getX() == actBlock.getLocation().getBlock().getX() && shotArrow.getLocation().getBlock().getZ() == actBlock.getLocation().getBlock().getZ()) {
                    limitCount--;
                    remote = true;
                    entitiesList.remove(i);
                }
            }
        }

        limitCount = limit;

        if (remote) {
            for (int i = 0; i < entitiesList.size() && limitCount != 0; i++) {
                if (entitiesList.get(i) instanceof Arrow) {
                    limitCount--;
                    Arrow shotArrow = (Arrow) entitiesList.get(i);
                    effectBlock = shotArrow.getLocation().getBlock();
                    setCircle(player, world, actBlock, effectBlock);
                }
            }
        } else
            setCircle(player, world, actBlock, effectBlock);
    }

    public static void setCircle(Player player, World world, Block actBlock, Block effectBlock) {
        Block northSin = actBlock.getRelative(0, 0, -3);
        Block southSin = actBlock.getRelative(0, 0, 3);
        Block eastSin = actBlock.getRelative(3, 0, 0);
        Block westSin = actBlock.getRelative(-3, 0, 0);
        int n = 0;
        int s = 0;
        int e = 0;
        int w = 0;
        int[] intArray = new int[4];
        if (northSin.getRelative(BlockFace.NORTH).getType() == Material.REDSTONE_WIRE)
            n++;
        if (northSin.getRelative(BlockFace.SOUTH).getType() == Material.REDSTONE_WIRE)
            n++;
        if (northSin.getRelative(BlockFace.EAST).getType() == Material.REDSTONE_WIRE)
            n++;
        if (northSin.getRelative(BlockFace.WEST).getType() == Material.REDSTONE_WIRE)
            n++;
        intArray[0] = n;
        if (southSin.getRelative(BlockFace.NORTH).getType() == Material.REDSTONE_WIRE)
            s++;
        if (southSin.getRelative(BlockFace.SOUTH).getType() == Material.REDSTONE_WIRE)
            s++;
        if (southSin.getRelative(BlockFace.EAST).getType() == Material.REDSTONE_WIRE)
            s++;
        if (southSin.getRelative(BlockFace.WEST).getType() == Material.REDSTONE_WIRE)
            s++;
        intArray[1] = s;
        if (eastSin.getRelative(BlockFace.NORTH).getType() == Material.REDSTONE_WIRE)
            e++;
        if (eastSin.getRelative(BlockFace.SOUTH).getType() == Material.REDSTONE_WIRE)
            e++;
        if (eastSin.getRelative(BlockFace.EAST).getType() == Material.REDSTONE_WIRE)
            e++;
        if (eastSin.getRelative(BlockFace.WEST).getType() == Material.REDSTONE_WIRE)
            e++;
        intArray[2] = e;
        if (westSin.getRelative(BlockFace.NORTH).getType() == Material.REDSTONE_WIRE)
            w++;
        if (westSin.getRelative(BlockFace.SOUTH).getType() == Material.REDSTONE_WIRE)
            w++;
        if (westSin.getRelative(BlockFace.EAST).getType() == Material.REDSTONE_WIRE)
            w++;
        if (westSin.getRelative(BlockFace.WEST).getType() == Material.REDSTONE_WIRE)
            w++;
        intArray[3] = w;
        Arrays.sort(intArray);
        String arrayString = Arrays.toString(intArray);
        try {
            setCircleEffects(player, world, actBlock, effectBlock, arrayString);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void setCircleEffects(Player player, World world, Block actBlock, Block effectBlock, String arrayString) throws IOException {
        Location effectBlockLocation = effectBlock.getLocation();
        int cost = 0;
        if (!hasLearnedCircle(player, arrayString)) {
            if (learnCircle(player, arrayString, actBlock)) {
                player.sendMessage("You have successfully learned the circle " + arrayString);
                return;
            }
        }
        if (arrayString.equals("0"))
            return;

// Weapon Circle
        if (arrayString.equals("[1, 1, 1, 1]") && player.hasPermission("geometricmagic.set.1111")) {
            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            }
// Insure to account for philosophers stone modifier before setting cap
            cost = (int) (GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.1222.cost") * GeometricMagic.philosopherStoneModifier(player));
            if (cost > 20)
                cost = 20;

            if (player.getFoodLevel() >= (cost)) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost)));
                }
// Conversion over to Material instead of itemID
                Material itemMat = Material.getMaterial(GeometricMagicPlayerListener.plugin.getConfig().getString("setcircles.1111.item"));
                //int itemID = plugin.getConfig().getInt("setcircles.1111.item");
                ItemStack item = new ItemStack(itemMat);
                effectBlock.getWorld().dropItem(effectBlockLocation, item);

            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        }
// Repair Circle
        else if (arrayString.equals("[1, 1, 3, 3]") && player.hasPermission("geometricmagic.set.1133")) {
            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            }

            if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                }
            }
// Takes effectBlock location and resets durability of any items within radius
            int count = 0;
            for (Entity repairEntity : GeometricMagic.nearbyItems(effectBlock)) {
                if (repairEntity instanceof Item) {
                    Item droppedItem = (Item) repairEntity;

//Old method, new method takes location based on effect block instead of player, resulting in the affected items being more consistent
//for (int i = 0; i < repairEntities.size(); i++) {
                    //	if (repairEntities.get(i) instanceof Item) {
                    //		Item droppedItem = (Item) repairEntities.get(i);

                    // No longer taking itemId() into account with a massive check; We already know it's an instanceof Item, so its itemStack() has a durability.
                    int itemDurability = droppedItem.getItemStack().getDurability();
                    // Get cost
// Checking for non-zeroes keeps us from doing something stupid, like trying to repair a potion
                    if (itemDurability > 0) {
                        cost = itemDurability / 50;

//Ensures that the appropriate value is used when determining maximum cost amounts
                        int philCost = (int) (cost * GeometricMagic.philosopherStoneModifier(player));
                        if (philCost > 20)
                            philCost = 20;

                        if (player.getFoodLevel() >= (philCost)) {
                            if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                                player.setFoodLevel((int) (player.getFoodLevel() - (philCost)));
                            }

                            //ItemStack newItem = new ItemStack(itemCode, 1);

                            // enchant the item - Not needed anymore
                            //newItem.addEnchantments(effects);

//Reset durability to 0 and display an effect to show that items have been affected
                            droppedItem.getWorld().playEffect(droppedItem.getLocation(), Effect.SMOKE, 4);
                            droppedItem.getItemStack().setDurability((short) 0);
                            //effectBlock.getWorld().dropItem(effectBlockLocation, newItem);
                            count++;
                        } else {
                            player.sendMessage("You feel so hungry...");
                            if (count > 0)
// If the lightning is called from somewhere else, what are we doing calling it here as well? Especially if we didn't even do anything...
                                //effectBlock.getWorld().strikeLightningEffect(effectBlockLocation);
                                return;
                        }
                    }
                }
            }
// Conversion Circle
        } else if (arrayString.equals("[1, 2, 2, 2]") && player.hasPermission("geometricmagic.set.1222")) {
//Set cost to modified value before doing anything with it. This reduces the calls to philosophersStoneModifier()
            cost = (int) (GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.1222.cost") * GeometricMagic.philosopherStoneModifier(player));
            // Why set a maximum before accounting for phil modifier? If the config is wrong, fix it, not the value!
// This potentially allows for config setters to make certain circles require the philosophers stone to meet the energy cost requirements
//if (cost > 20)
            //	cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            }

            if (player.getFoodLevel() >= (cost)) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost)));
                }

                //ItemStack oneRedstone = new ItemStack(Material.REDSTONE, 1);
                //Item redStack = effectBlock.getWorld().dropItem(effectBlockLocation, oneRedstone);
                //List<Entity> entityList = redStack.getNearbyEntities(5, 10, 5);

                for (Entity convertEnt : GeometricMagic.nearbyItems(effectBlock)) {
                    if (convertEnt instanceof Item) {
                        Item droppedItem = (Item) convertEnt;

                        // Skip items because they don't have values - Unused
                        //if(droppedItem.getItemStack().getTypeId() > 255) {

// Check to see if the item is defined in the config file, it it isn't, disallow it
                        if (GeometricMagicPlayerListener.plugin.getConfig().getInt("values." + droppedItem.getItemStack().getTypeId() + ".0") == 0) {
                            player.sendMessage("You can't convert this item");
                            continue;
                        }

                        // check if player has permission to break blocks here first
                        if (!GeometricMagic.checkBlockBreakSimulation(droppedItem.getLocation(), player)) {
                            // player.sendMessage("You don't have permission to do that there.");
                            return;
                        }

                        int valueArray = GeometricMagic.getBlockValue(GeometricMagicPlayerListener.plugin, droppedItem.getItemStack().getTypeId(), droppedItem.getItemStack().getData().getData());

                        int pay = (valueArray * droppedItem.getItemStack().getAmount());
                        if (GeometricMagic.getTransmutationCostSystem(GeometricMagicPlayerListener.plugin).equalsIgnoreCase("vault")) {
                            Economy econ = GeometricMagic.getEconomy();
                            if (pay > 0) {
                                econ.depositPlayer(player.getName(), pay);
                                player.sendMessage("Deposited " + pay + " " + econ.currencyNamePlural() + " for " + droppedItem.getItemStack().getType().name().toLowerCase().replace('_', ' '));
                                droppedItem.remove();
                            } else if (pay < 0) {
                                econ.withdrawPlayer(player.getName(), pay * -1);
                                player.sendMessage("Withdrew " + pay + " " + econ.currencyNamePlural() + " for " + droppedItem.getItemStack().getType().name().toLowerCase().replace('_', ' '));
                                droppedItem.remove();
                            }
                        } else if (GeometricMagic.getTransmutationCostSystem(GeometricMagicPlayerListener.plugin).equalsIgnoreCase("xp")) {
                            player.setLevel((valueArray * droppedItem.getItemStack().getAmount()) + player.getLevel());
                            droppedItem.remove();
                        }

                        /*
                         * player.setLevel((valueArray[droppedItem.getItemStack()
                         * .getTypeId()] * droppedItem.getItemStack()
                         * .getAmount()) + player.getLevel());
                         */
                    }
                }
            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
// Philosophers Stone Circle
        } else if (arrayString.equals("[1, 2, 3, 3]") && player.hasPermission("geometricmagic.set.1233")) {

            cost = (int) (GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.1233.cost") * GeometricMagic.philosopherStoneModifier(player));
            if (cost > 20)
                cost = 20;
            GeometricMagicPlayerListener.plugin.getLogger().info(String.valueOf(cost));
            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            }
            int fires = 0;
            int radius = 2;
            List<Entity> entityList = effectBlock.getWorld().getEntities();
/*
Despite how complex it looks, this section simply takes and looks at each stack of fire, adding them together to equal 64.
If it gets to 64 and still has leftover, it simply sets the last stack to the leftover amount. On the clients end, this looks far
cleaner than spawning a new stack, and allows for a bit cleaner flow control.

We achieve this by adding the stacks we have iterated over into an ArrayList (toBeRemoved), except if our total would go above 64.
In that case, we simply set the last stack to equal the remains, and ensure that fire = 64
*/
            if (player.getFoodLevel() >= (cost)) {
                List<Item> toBeRemoved = new ArrayList<Item>();
                for (int i = 0; i < entityList.size(); i++) {
                    if (entityList.get(i) instanceof Item && entityList.get(i).getLocation().distance(effectBlockLocation) <= radius) {
                        Item sacrifice = (Item) entityList.get(i);
// Not sure why we are doing this, but we are checking to see if they can actually break blocks here...
                        if (!GeometricMagic.checkBlockBreakSimulation(sacrifice.getLocation(), player)) {
                            return;
                        }
// Main chunk of sacrifice checking here
                        if (sacrifice.getItemStack().getType() == Material.FIRE) {
                            fires += sacrifice.getItemStack().getAmount();
                            if (fires > 64) {
// Handles if we go over. fires should always equal 64 after this, or else we haven't gotten anywhere.
                                int extraFires = fires - 64;
                                fires = 64;
// Sets stack size for the last stack we iterated over.
                                sacrifice.getItemStack().setAmount(extraFires);
                                sacrifice.getWorld().playEffect(sacrifice.getLocation(), Effect.SMOKE, 4);
                            } else {
// Adds the last stack we completely used to a list of stacks waiting to be removed
                                toBeRemoved.add(sacrifice);
                            }
                            if (fires == 64) {
                                fires -= 64;
                                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                                    player.setFoodLevel((player.getFoodLevel() - (cost)));
                                    effectBlock.getWorld().dropItem(effectBlockLocation, new ItemStack(Material.PORTAL, 1));
                                    Iterator<Item> toBeRemovedit = toBeRemoved.iterator();
                                    while (toBeRemovedit.hasNext()) {
// Iterate over the list and remove each stack we used
                                        Item sacrificed = toBeRemovedit.next();
                                        sacrificed.remove();
                                    }
                                }
                            }
                        }
                    }
                }
            }
// Boron Circle
        } else if (arrayString.equals("[1, 2, 3, 4]") && player.hasPermission("geometricmagic.set.1234")) {

            cost = (int) (GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.1234.cost") * GeometricMagic.philosopherStoneModifier(player));
            if (cost > 20)
                cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            }
            if (player.getFoodLevel() >= (cost)) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost)));
                }
                player.sendMessage(ChatColor.GREEN + "The four elements, like man alone, are weak. But together they form the strong fifth element: boron -Brother Silence");
                int amount = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.1234.amount");
                ItemStack oneRedstone = new ItemStack(Material.REDSTONE, amount);

                effectBlock.getWorld().dropItem(effectBlockLocation, oneRedstone);

            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
// Soul Circle
        } else if (arrayString.equals("[2, 2, 2, 3]") && player.hasPermission("geometricmagic.set.2223")) {

            cost = (int) (GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.2223.cost") * GeometricMagic.philosopherStoneModifier(player));
            if (cost > 20)
                cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            }
            ItemStack oneRedstone = new ItemStack(Material.REDSTONE, 1);
            if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                }
                Item redStack = effectBlock.getWorld().dropItem(effectBlockLocation, oneRedstone);
                int size = setCircleSize(actBlock);
                List<Entity> entityList = redStack.getNearbyEntities(size + 5, 128, size + 5);

                for (int i = 0; i < entityList.size(); i++) {
                    if (entityList.get(i) instanceof Player) {
                        HumanEntity victim = (HumanEntity) entityList.get(i);
                        Location victimLocation = victim.getLocation();

                        if (!victim.equals(player)) {
                            victim.getWorld().strikeLightningEffect(victimLocation);
                            if (victim.getInventory().contains(Material.FIRE)) {
                                for (int k = 0; k < player.getInventory().getSize(); k++) {
                                    if (player.getInventory().getItem(i).getType() == Material.FIRE) {
                                        // System.out.println("removed a fire");
                                        int amount = player.getInventory().getItem(k).getAmount();
                                        player.getInventory().getItem(k).setAmount(amount - 1);
                                        if (amount - 1 <= 0) {
                                            player.getInventory().clear(k);
                                        }
                                    }
                                }
                            } else
                                victim.damage(20);
                            if (victim.isDead()) {
                                ItemStack oneFire = new ItemStack(51, 1);
                                victim.getWorld().dropItem(actBlock.getLocation(), oneFire);
                            }
                        }
                    }
                    if (entityList.get(i) instanceof Villager) {
                        Villager victim = (Villager) entityList.get(i);
                        victim.getWorld().strikeLightningEffect(victim.getLocation());
                        victim.damage(20);
                        if (victim.isDead()) {
                            ItemStack oneFire = new ItemStack(51, 1);
                            victim.getWorld().dropItem(actBlock.getLocation(), oneFire);
                        }
                    }
                }
                redStack.remove();
            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        } else if (arrayString.equals("[2, 2, 2, 4]") && player.hasPermission("geometricmagic.set.2224")) {

            cost = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.2224.cost");
            if (cost > 20)
                cost = 20;

            if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                }
                Location spawnLoc = effectBlockLocation;
                spawnLoc.add(0.5, 1, 0.5);
                effectBlock.getWorld().spawn(spawnLoc, Enderman.class);
            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        } else if (arrayString.equals("[2, 2, 4, 4]") && player.hasPermission("geometricmagic.set.2244")) {
            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            }

            cost = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.2244.cost");
            if (cost > 20)
                cost = 20;

            Location actPoint = effectBlockLocation;
            int na = 0, nb = 0, ea = 0, eb = 0, sa = 0, sb = 0, wa = 0, wb = 0;
            Block curBlock = effectBlock.getRelative(0, 0, -1);
            while (curBlock.getRelative(0, 0, -1).getType() == Material.REDSTONE_WIRE) {
                na++;
                curBlock = curBlock.getRelative(0, 0, -1);
            }
            Block fineBlock = curBlock;
            while (fineBlock.getRelative(-1, 0, 0).getType() == Material.REDSTONE_WIRE) {
                nb++;
                fineBlock = fineBlock.getRelative(-1, 0, 0);
            }
            curBlock = effectBlock.getRelative(1, 0, 0);
            while (curBlock.getRelative(1, 0, 0).getType() == Material.REDSTONE_WIRE) {
                ea++;
                curBlock = curBlock.getRelative(1, 0, 0);
            }
            fineBlock = curBlock;
            while (fineBlock.getRelative(0, 0, -1).getType() == Material.REDSTONE_WIRE) {
                eb++;
                fineBlock = fineBlock.getRelative(0, 0, -1);
            }
            curBlock = effectBlock.getRelative(0, 0, 1);
            while (curBlock.getRelative(0, 0, 1).getType() == Material.REDSTONE_WIRE) {
                sa++;
                curBlock = curBlock.getRelative(0, 0, 1);
            }
            fineBlock = curBlock;
            while (fineBlock.getRelative(1, 0, 0).getType() == Material.REDSTONE_WIRE) {
                sb++;
                fineBlock = fineBlock.getRelative(1, 0, 0);
            }
            curBlock = effectBlock.getRelative(-1, 0, 0);
            while (curBlock.getRelative(-1, 0, 0).getType() == Material.REDSTONE_WIRE) {
                wa++;
                curBlock = curBlock.getRelative(-1, 0, 0);
            }
            fineBlock = curBlock;
            while (fineBlock.getRelative(0, 0, 1).getType() == Material.REDSTONE_WIRE) {
                wb++;
                fineBlock = fineBlock.getRelative(0, 0, 1);
            }
            // north negative z, south positive z, east positive x, west
            // negative x
            int z = ((sa * 100 + sb) - (na * 100 + nb));
            int x = ((ea * 100 + eb) - (wa * 100 + wb));
            double actPointX = actPoint.getX();
            double actPointZ = actPoint.getZ();
            Location teleLoc = actPoint.add(x, 0, z);
            double distance = Math.sqrt(Math.pow(teleLoc.getX() - actPointX, 2) + Math.pow(teleLoc.getZ() - actPointZ, 2));
            double mathRandX = GeometricMagic.philosopherStoneModifier(player) * distance / 10 * Math.random();
            double mathRandZ = GeometricMagic.philosopherStoneModifier(player) * distance / 10 * Math.random();
            double randX = (teleLoc.getX() - (0.5 * mathRandX)) + (mathRandX);
            double randZ = (teleLoc.getZ() - (0.5 * mathRandZ)) + (mathRandZ);
            teleLoc.setX(randX);
            teleLoc.setZ(randZ);
            while (teleLoc.getWorld().getChunkAt(teleLoc).isLoaded() == false) {
                teleLoc.getWorld().getChunkAt(teleLoc).load(true);
            }
            int highestBlock = teleLoc.getWorld().getHighestBlockYAt(teleLoc) + 1;
            // System.out.println( mathRandX + " " + mathRandZ );
            player.sendMessage("Safe teleportation altitude is at " + highestBlock);
            return;
        } else if (arrayString.equals("[2, 3, 3, 3]") && player.hasPermission("geometricmagic.set.2333")) {

            cost = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.2333.cost");
            if (cost > 20)
                cost = 20;

            int size = setCircleSize(actBlock);
            cost = cost + size / 2;

            // Make sure cost is not more than 20
            if (cost > 20)
                cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            }
            if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                }

                // check if player has permission to break blocks here first
                if (!GeometricMagic.checkBlockBreakSimulation(effectBlockLocation, player)) {
                    // player.sendMessage("You don't have permission to do that there.");
                    return;
                }

                Fireball fireball = effectBlockLocation.getWorld().spawn(effectBlockLocation, Fireball.class);
                fireball.setIsIncendiary(false);
                fireball.setYield(4 + size);
            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        } else if (arrayString.equals("[3, 3, 3, 4]") && player.hasPermission("geometricmagic.set.3334")) {

            cost = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.3334.cost");
            if (cost > 20)
                cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            }
            if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                }

                Integer circleSize = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.3334.size");

                GeometricMagicPlayerListener.alchemyFiller(Material.AIR, (byte) 0, Material.FIRE, (byte) 0, effectBlock.getRelative((circleSize / 2) * -1, 0, (circleSize / 2) * -1).getLocation(),
                        effectBlock.getRelative(circleSize / 2, circleSize, circleSize / 2).getLocation(), player, false);

            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        } else if (arrayString.equals("[3, 3, 4, 4]") && player.hasPermission("geometricmagic.set.3344")) {

            cost = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.3344.cost");
            if (cost > 20)
                cost = 20;

            int size = setCircleSize(actBlock);
            cost = cost + size / 2;

            // Make sure cost is not more than 20
            if (cost > 20)
                cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            }
            if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                }

                // check if player has permission to break blocks here first
                if (!GeometricMagic.checkBlockBreakSimulation(effectBlockLocation, player)) {
                    // player.sendMessage("You don't have permission to do that there.");
                    return;
                }

                Fireball fireball = effectBlockLocation.getWorld().spawn(effectBlockLocation, Fireball.class);
                fireball.setIsIncendiary(true);
                fireball.setYield(4 + size);
            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        } else if (arrayString.equals("[3, 4, 4, 4]") && player.hasPermission("geometricmagic.set.3444")) {

            cost = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.3444.cost");
            if (cost > 20)
                cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            }
            if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                try {
                    if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                        player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                    }
                    humanTransmutation(player);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        } else if (arrayString.equals("[0, 1, 1, 1]") && player.hasPermission("geometricmagic.set.0111")) {

            // using x111 because yml doesn't like 0 as first character
            cost = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.x111.cost");
            if (cost > 20)
                cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            } else if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                }
                Location playerSpawn = player.getBedSpawnLocation();
                if (playerSpawn != null) {
                    if (playerSpawn.getBlock().getType() == Material.AIR) {
                        player.teleport(playerSpawn);
                    } else {
                        if (new Location(player.getWorld(), playerSpawn.getX() + 1, playerSpawn.getY(), playerSpawn.getZ()).getBlock().getType() == Material.AIR) {
                            player.teleport(new Location(player.getWorld(), playerSpawn.getX() + 1, playerSpawn.getY(), playerSpawn.getZ()));
                        } else if (new Location(player.getWorld(), playerSpawn.getX() - 1, playerSpawn.getY(), playerSpawn.getZ()).getBlock().getType() == Material.AIR) {
                            player.teleport(new Location(player.getWorld(), playerSpawn.getX() - 1, playerSpawn.getY(), playerSpawn.getZ()));
                        } else if (new Location(player.getWorld(), playerSpawn.getX(), playerSpawn.getY(), playerSpawn.getZ() + 1).getBlock().getType() == Material.AIR) {
                            player.teleport(new Location(player.getWorld(), playerSpawn.getX(), playerSpawn.getY(), playerSpawn.getZ() + 1));
                        } else if (new Location(player.getWorld(), playerSpawn.getX(), playerSpawn.getY(), playerSpawn.getZ() - 1).getBlock().getType() == Material.AIR) {
                            player.teleport(new Location(player.getWorld(), playerSpawn.getX(), playerSpawn.getY(), playerSpawn.getZ() - 1));
                        } else {
                            player.sendMessage("Your bed is not safe to teleport to!");
                            if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                                player.setFoodLevel((int) (player.getFoodLevel() + (cost * GeometricMagic.philosopherStoneModifier(player))));
                            }
                        }
                    }
                } else {
                    player.sendMessage("You do not have a spawn set!");
                    if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                        player.setFoodLevel((int) (player.getFoodLevel() + (cost * GeometricMagic.philosopherStoneModifier(player))));
                    }
                }
            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        } else if (arrayString.equals("[0, 0, 4, 4]") && player.hasPermission("geometricmagic.set.0044")) {

            // using x044 because yml doesn't like 0 as first character
            cost = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.x044.cost");
            if (cost > 20)
                cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            } else if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                }
                Location spawnLoc = effectBlockLocation;

                // check if player has permission to break blocks here first
                if (!GeometricMagic.checkBlockBreakSimulation(spawnLoc, player)) {
                    // player.sendMessage("You don't have permission to do that there.");
                    return;
                }

                spawnLoc.add(0.5, 1, 0.5);
                effectBlock.getWorld().spawn(spawnLoc, Pig.class);
            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        } else if (arrayString.equals("[0, 1, 4, 4]") && player.hasPermission("geometricmagic.set.0144")) {

            // using x144 because yml doesn't like 0 as first character
            cost = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.x144.cost");
            if (cost > 20)
                cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            } else if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                }
                Location spawnLoc = effectBlockLocation;

                // check if player has permission to break blocks here first
                if (!GeometricMagic.checkBlockBreakSimulation(spawnLoc, player)) {
                    // player.sendMessage("You don't have permission to do that there.");
                    return;
                }

                spawnLoc.add(0.5, 1, 0.5);
                effectBlock.getWorld().spawn(spawnLoc, Sheep.class);
            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        } else if (arrayString.equals("[0, 2, 4, 4]") && player.hasPermission("geometricmagic.set.0244")) {

            // using x244 because yml doesn't like 0 as first character
            cost = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.x244.cost");
            if (cost > 20)
                cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            } else if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                }
                Location spawnLoc = effectBlockLocation;

                // check if player has permission to break blocks here first
                if (!GeometricMagic.checkBlockBreakSimulation(spawnLoc, player)) {
                    // player.sendMessage("You don't have permission to do that there.");
                    return;
                }

                spawnLoc.add(0.5, 1, 0.5);
                effectBlock.getWorld().spawn(spawnLoc, Cow.class);
            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        } else if (arrayString.equals("[0, 3, 4, 4]") && player.hasPermission("geometricmagic.set.0344")) {

            // using x344 because yml doesn't like 0 as first character
            cost = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.x344.cost");
            if (cost > 20)
                cost = 20;

            if (!hasLearnedCircle(player, arrayString)) {
                player.sendMessage("You have not yet learned circle " + arrayString + "!");
                return;
            } else if (player.getFoodLevel() >= (cost * GeometricMagic.philosopherStoneModifier(player))) {
                if (!player.hasPermission("geometricmagic.bypass.hunger")) {
                    player.setFoodLevel((int) (player.getFoodLevel() - (cost * GeometricMagic.philosopherStoneModifier(player))));
                }
                Location spawnLoc = effectBlockLocation;

                // check if player has permission to break blocks here first
                if (!GeometricMagic.checkBlockBreakSimulation(spawnLoc, player)) {
                    // player.sendMessage("You don't have permission to do that there.");
                    return;
                }

                spawnLoc.add(0.5, 1, 0.5);
                effectBlock.getWorld().spawn(spawnLoc, Chicken.class);
            } else {
                player.sendMessage("You feel so hungry...");
                return;
            }
        } else {
            player.sendMessage("You do not have permission to use " + arrayString + " or set circle does not exist");
        }
        effectBlock.getWorld().strikeLightningEffect(effectBlockLocation);
    }

    public static int setCircleSize(Block actBlock) {
        // limit sizes
        int limitsize = GeometricMagicPlayerListener.plugin.getConfig().getInt("setcircles.limitsize");

        int na = 0, nb = 0, ea = 0, eb = 0, sa = 0, sb = 0, wa = 0, wb = 0, nc = 0, ec = 0, sc = 0, wc = 0;
        Block curBlock = actBlock.getRelative(0, 0, -5);
        while (curBlock.getRelative(0, 0, -1).getType() == Material.REDSTONE_WIRE || na == limitsize) {
            na++;
            curBlock = curBlock.getRelative(0, 0, -1);
        }
        Block fineBlock = curBlock;
        while (fineBlock.getRelative(-1, 0, 0).getType() == Material.REDSTONE_WIRE || nb == limitsize) {
            nb++;
            fineBlock = fineBlock.getRelative(-1, 0, 0);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(1, 0, 0).getType() == Material.REDSTONE_WIRE || nc == limitsize) {
            nc++;
            fineBlock = fineBlock.getRelative(1, 0, 0);
        }
        curBlock = actBlock.getRelative(5, 0, 0);
        while (curBlock.getRelative(1, 0, 0).getType() == Material.REDSTONE_WIRE || ea == limitsize) {
            ea++;
            curBlock = curBlock.getRelative(1, 0, 0);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(0, 0, -1).getType() == Material.REDSTONE_WIRE || eb == limitsize) {
            eb++;
            fineBlock = fineBlock.getRelative(0, 0, -1);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(0, 0, 1).getType() == Material.REDSTONE_WIRE || ec == limitsize) {
            ec++;
            fineBlock = fineBlock.getRelative(0, 0, 1);
        }
        curBlock = actBlock.getRelative(0, 0, 5);
        while (curBlock.getRelative(0, 0, 1).getType() == Material.REDSTONE_WIRE || sa == limitsize) {
            sa++;
            curBlock = curBlock.getRelative(0, 0, 1);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(1, 0, 0).getType() == Material.REDSTONE_WIRE || sb == limitsize) {
            sb++;
            fineBlock = fineBlock.getRelative(1, 0, 0);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(-1, 0, 0).getType() == Material.REDSTONE_WIRE || sc == limitsize) {
            sc++;
            fineBlock = fineBlock.getRelative(-1, 0, 0);
        }
        curBlock = actBlock.getRelative(-5, 0, 0);
        while (curBlock.getRelative(-1, 0, 0).getType() == Material.REDSTONE_WIRE || wa == limitsize) {
            wa++;
            curBlock = curBlock.getRelative(-1, 0, 0);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(0, 0, 1).getType() == Material.REDSTONE_WIRE || wb == limitsize) {
            wb++;
            fineBlock = fineBlock.getRelative(0, 0, 1);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(0, 0, -1).getType() == Material.REDSTONE_WIRE || wc == limitsize) {
            wc++;
            fineBlock = fineBlock.getRelative(0, 0, -1);
        }
        int size = 0;
        if (wa == ea && na == sa && wb == eb && nb == sb && wc == ec && nc == sc && wa == na) {
            size = wa;
        }
        return size;
    }

    public static boolean hasLearnedCircle(Player player, String circle) throws IOException {
        File myFile = new File("plugins/GeometricMagic/" + player.getName() + ".txt");
        if (!myFile.exists()) {
            return false;
        }
        Scanner inputFile = new Scanner(myFile);
        while (inputFile.hasNextLine()) {
            String name = inputFile.nextLine();
            if (name.equals(circle)) {
                inputFile.close();
                return true;
            }
        }
        inputFile.close();
        return false;
    }

    public static boolean learnCircle(Player player, String circle, Block actBlock) throws IOException {
        boolean status = false;
        // System.out.println("learnCircle");
        ItemStack oneRedstone = new ItemStack(331, 1);
        Item redStack = actBlock.getWorld().dropItem(actBlock.getLocation(), oneRedstone);
        List<Entity> entityList = redStack.getNearbyEntities(2, 10, 2);
        for (int i = 0; i < entityList.size(); i++) {
            if (entityList.get(i) instanceof Enderman) {
                if (new File("plugins/GeometricMagic/").mkdirs())
                    System.out.println("[GeometricMagic] File created for " + player.getName());
                File myFile = new File("plugins/GeometricMagic/" + player.getName() + ".txt");
                if (myFile.exists()) {
                    FileWriter fWriter = new FileWriter("plugins/GeometricMagic/" + player.getName() + ".txt", true);
                    PrintWriter outputFile = new PrintWriter(fWriter);
                    outputFile.println(circle);
                    outputFile.close();
                } else {
                    PrintWriter outputFile = new PrintWriter("plugins/GeometricMagic/" + player.getName() + ".txt");
                    outputFile.println(circle);
                    outputFile.close();
                }
                status = true;
            }
        }
        redStack.remove();
        return status;
    }

    public static void humanTransmutation(Player player) throws IOException {
        if (new File("plugins/GeometricMagic/").mkdirs())
            GeometricMagicPlayerListener.plugin.getLogger().info("Sacrifices file created.");
        File myFile = new File("plugins/GeometricMagic/sacrifices.txt");
        if (myFile.exists()) {
            Scanner inputFile = new Scanner(myFile);
            while (inputFile.hasNextLine()) {
                String name = inputFile.nextLine();
                if (name.equals(player.getDisplayName())) {
                    FileWriter dWriter = new FileWriter("plugins/GeometricMagic/sacrificed.txt", true);
                    PrintWriter dFile = new PrintWriter(dWriter);
                    dFile.println(player.getDisplayName());
                    dFile.close();
                    return;
                }
            }
            inputFile.close();
        } else {
            PrintWriter outputFile = new PrintWriter("plugins/GeometricMagic/sacrifices.txt");
            GeometricMagicPlayerListener.plugin.getLogger().info("Sacrifices file created.");
            outputFile.close();
        }
        FileWriter fWriter = new FileWriter("plugins/GeometricMagic/sacrifices.txt", true);
        PrintWriter outputFile = new PrintWriter(fWriter);
        outputFile.println(player.getDisplayName());
        outputFile.println(0);
        player.sendMessage("You have committed the taboo! Crafting is your sacrifice, knowledge your reward.");
        outputFile.close();
    }
}
