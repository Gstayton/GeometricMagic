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

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GeometricMagicTeleportationCircle {
    public static void teleportationCircle(Player player, World world, Block actBlock) {

        // activation block in center
        Location actPoint = actBlock.getLocation();

        // init some variables
        int na = 0, nb = 0, ea = 0, eb = 0, sa = 0, sb = 0, wa = 0, wb = 0, nc = 0, ec = 0, sc = 0, wc = 0;

        // set core blocks to air
        actBlock.setType(Material.AIR);
        actBlock.getRelative(1, 0, 0).setType(Material.AIR);
        actBlock.getRelative(-1, 0, 0).setType(Material.AIR);
        actBlock.getRelative(0, 0, 1).setType(Material.AIR);
        actBlock.getRelative(0, 0, -1).setType(Material.AIR);

        // count and track all redstone blocks
        Block curBlock = actBlock.getRelative(0, 0, -1);
        while (curBlock.getRelative(0, 0, -1).getType() == Material.REDSTONE_WIRE) {
            na++;
            curBlock = curBlock.getRelative(0, 0, -1);
        }
        Block fineBlock = curBlock;
        while (fineBlock.getRelative(-1, 0, 0).getType() == Material.REDSTONE_WIRE) {
            nb++;
            fineBlock = fineBlock.getRelative(-1, 0, 0);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(1, 0, 0).getType() == Material.REDSTONE_WIRE) {
            nc++;
            fineBlock = fineBlock.getRelative(1, 0, 0);
        }

        curBlock = actBlock.getRelative(1, 0, 0);
        while (curBlock.getRelative(1, 0, 0).getType() == Material.REDSTONE_WIRE) {
            ea++;
            curBlock = curBlock.getRelative(1, 0, 0);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(0, 0, -1).getType() == Material.REDSTONE_WIRE) {
            eb++;
            fineBlock = fineBlock.getRelative(0, 0, -1);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(0, 0, 1).getType() == Material.REDSTONE_WIRE) {
            ec++;
            fineBlock = fineBlock.getRelative(0, 0, 1);
        }

        curBlock = actBlock.getRelative(0, 0, 1);
        while (curBlock.getRelative(0, 0, 1).getType() == Material.REDSTONE_WIRE) {
            sa++;
            curBlock = curBlock.getRelative(0, 0, 1);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(1, 0, 0).getType() == Material.REDSTONE_WIRE) {
            sb++;
            fineBlock = fineBlock.getRelative(1, 0, 0);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(-1, 0, 0).getType() == Material.REDSTONE_WIRE) {
            sc++;
            fineBlock = fineBlock.getRelative(-1, 0, 0);
        }

        curBlock = actBlock.getRelative(-1, 0, 0);
        while (curBlock.getRelative(-1, 0, 0).getType() == Material.REDSTONE_WIRE) {
            wa++;
            curBlock = curBlock.getRelative(-1, 0, 0);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(0, 0, 1).getType() == Material.REDSTONE_WIRE) {
            wb++;
            fineBlock = fineBlock.getRelative(0, 0, 1);
        }
        fineBlock = curBlock;
        while (fineBlock.getRelative(0, 0, -1).getType() == Material.REDSTONE_WIRE) {
            wc++;
            fineBlock = fineBlock.getRelative(0, 0, -1);
        }

        // set all redstone to air
        curBlock = actBlock.getRelative(0, 0, -1);
        for (int c = 0; c < na; c++) {
            curBlock = curBlock.getRelative(0, 0, -1);
            curBlock.setType(Material.AIR);
        }
        fineBlock = curBlock;
        for (int c = 0; c < nb; c++) {
            fineBlock = fineBlock.getRelative(-1, 0, 0);
            fineBlock.setType(Material.AIR);
        }
        fineBlock = curBlock;
        for (int c = 0; c < nc; c++) {
            fineBlock = fineBlock.getRelative(1, 0, 0);
            fineBlock.setType(Material.AIR);
        }

        curBlock = actBlock.getRelative(1, 0, 0);
        for (int c = 0; c < ea; c++) {
            curBlock = curBlock.getRelative(1, 0, 0);
            curBlock.setType(Material.AIR);
        }
        fineBlock = curBlock;
        for (int c = 0; c < eb; c++) {
            fineBlock = fineBlock.getRelative(0, 0, -1);
            fineBlock.setType(Material.AIR);
        }
        fineBlock = curBlock;
        for (int c = 0; c < ec; c++) {
            fineBlock = fineBlock.getRelative(0, 0, 1);
            fineBlock.setType(Material.AIR);
        }

        curBlock = actBlock.getRelative(0, 0, 1);
        for (int c = 0; c < sa; c++) {
            curBlock = curBlock.getRelative(0, 0, 1);
            curBlock.setType(Material.AIR);
        }
        fineBlock = curBlock;
        for (int c = 0; c < sb; c++) {
            fineBlock = fineBlock.getRelative(1, 0, 0);
            fineBlock.setType(Material.AIR);
        }
        fineBlock = curBlock;
        for (int c = 0; c < sc; c++) {
            fineBlock = fineBlock.getRelative(-1, 0, 0);
            fineBlock.setType(Material.AIR);
        }

        curBlock = actBlock.getRelative(-1, 0, 0);
        for (int c = 0; c < wa; c++) {
            curBlock = curBlock.getRelative(-1, 0, 0);
            curBlock.setType(Material.AIR);
        }
        fineBlock = curBlock;
        for (int c = 0; c < wb; c++) {
            fineBlock = fineBlock.getRelative(0, 0, 1);
            fineBlock.setType(Material.AIR);
        }
        fineBlock = curBlock;
        for (int c = 0; c < wc; c++) {
            fineBlock = fineBlock.getRelative(0, 0, -1);
            fineBlock.setType(Material.AIR);
        }

        // find out teleport location and modify it

        // north negative z
        // south positive z
        // east positive x
        // west negative x
        int z = ((sa * 100 + sb * 10) - (na * 100 + nb * 10));
        int x = ((ea * 100 + eb * 10) - (wa * 100 + wb * 10));
        int y = nc + ec + sc + wc;

        double actPointX = actPoint.getX();
        double actPointZ = actPoint.getZ();

        Location teleLoc = actPoint.add(x, y, z);

        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        teleLoc.setYaw(yaw);
        teleLoc.setPitch(pitch);

        double distance = Math.sqrt(Math.pow(teleLoc.getX() - actPointX, 2) + Math.pow(teleLoc.getZ() - actPointZ, 2));

        double mathRandX = GeometricMagic.philosopherStoneModifier(player) * distance / 10 * Math.random();
        double mathRandZ = GeometricMagic.philosopherStoneModifier(player) * distance / 10 * Math.random();

        double randX = (teleLoc.getX() - (0.5 * mathRandX)) + (mathRandX);
        double randZ = (teleLoc.getZ() - (0.5 * mathRandZ)) + (mathRandZ);

        teleLoc.setX(randX);
        teleLoc.setZ(randZ);

        // wait for chunk to be loaded before teleporting player
        //while (teleLoc.getWorld().getChunkAt(teleLoc).isLoaded() == false) {
        // teleLoc.getWorld().getChunkAt(teleLoc).load(true);
        //}
        // hopefully a bit more efficient
        Chunk chunk = teleLoc.getChunk();
        while (!chunk.isLoaded() || !chunk.load(true)) ;

        // teleport player
        player.teleport(teleLoc);

        player.getInventory().addItem(new ItemStack(Material.REDSTONE, 5 + na + nb + nc + sa + sb + sc + ea + eb + ec + wa + wb + wc));

        actBlock.getWorld().strikeLightningEffect(actBlock.getLocation());
        actBlock.getWorld().strikeLightningEffect(teleLoc);
    }
}
