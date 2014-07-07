package me.kosannicholas.geometricmagic;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utilities {

    public static List<Item> getNearbyItems(Location origin, int radius) {
        Chunk[] chunks;
        ArrayList<Item> itemsList = new ArrayList<Item>();
        ArrayList<Entity> worldEntities = new ArrayList<Entity>();

        chunks = origin.getWorld().getLoadedChunks();

        for (Chunk chunk : chunks) {
            Location loc = new Location(origin.getWorld(), chunk.getX(), origin.getY(), chunk.getZ());
            if (origin.distance(loc) < 23) {
                for (Entity entity : Arrays.asList(chunk.getEntities())) {
                    worldEntities.add(entity);
                }
            }
        }

        for (Entity entity : worldEntities) {
            if (entity.getLocation().distance(origin) <= radius && entity instanceof Item) {
                itemsList.add((Item) entity);
            }
        }
        return itemsList;
    }

    public static List<Item> getNearbyItems(Location origin) {
        return getNearbyItems(origin, 2);
    }
}
