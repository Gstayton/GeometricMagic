package me.kosannicholas.geometricmagic.setcircles;

import me.kosannicholas.geometricmagic.Utilities;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

import java.util.List;


public class RepairCircle implements SetCircle {
    public void act(Block origin) {
        List<Item> itemList = Utilities.getNearbyItems(origin.getLocation());
        for (Item item : itemList) {
            // todo add cost / permission checks
            item.getWorld().playEffect(item.getLocation(), Effect.SMOKE, 4);
            item.getItemStack().setDurability((short) 0);
        }
    }
}

