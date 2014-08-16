package me.kosannicholas.geometricmagic.setcircles;

import me.kosannicholas.geometricmagic.Utilities;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

import java.util.List;


public class ConversionCircle implements SetCircle {
	public void act(Block origin) {
		List<Item> items = Utilities.getNearbyItems(origin.getLocation());
		for (Item item : items) {
			item.remove();
		}
	}
}
