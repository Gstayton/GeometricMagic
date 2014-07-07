package me.kosannicholas.geometricmagic.setcircles;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class WeaponCircle implements SetCircle {
    public void act(Block origin) {
        Material weapon = Material.getMaterial("IRON_SWORD");
        ItemStack item = new ItemStack(weapon);

        origin.getWorld().dropItem(origin.getLocation(), item);
    }
}
