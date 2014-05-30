package me.kosannicholas.GeometricMagic;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class TransmutationCircle {
	// Internal state shared between methods.
	// Final values determine location, size,
	// and orientation of circle (if one exists).
	private Block	block;
	private int 	radius;
	private XZ	dir;

	private enum Shell {
		SOLID,
		MIXED,
		FLUSH_TO_EDGE,
		EMPTY
	}

	private class XZ extends Object {
		public int x, z;
		public XZ(int X, int Z) {
			x = X;
			z = Z;
		}
		public void rotateCCW() {
			int X = x;
			x = -z;
			z = X;
		}
	}

	public static boolean isChalk(Block b) {
		return b.getType() == Material.REDSTONE_WIRE;
	}

	// True if a circle has been discovered.
	private boolean identified = false;
	
	private final GeometricMagicPlugin plugin;

	public TransmutationCircle(GeometricMagicPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean foundCircle() {
		return identified;
	}

	public int getInnerWidth() {
		return 2*radius - 1;
	}

	// Search for a valid circle from the initial block.
	public void search(Block clickedBlock) {
		block = clickedBlock;
		if (!isChalk(block)) {
			return;
		}
		dir	= new XZ(1, 0);
		// Four different directions to check.
		for (int n = 0; n < 4; n++, dir.rotateCCW()) {
			final int safety = 1000; // Guard against an infinite loop.
			expansionLoop:
			for (radius = 1; radius < safety; radius++) {
				switch (getShell(radius)) {
					case SOLID:
						identified = isCleanCircle();
						return;
					case FLUSH_TO_EDGE:
						// Keep trucking. Potentially hit edge
						// of larger circle. Increase radius.
						break;
					default:
						// Try next direction.
						break expansionLoop;
				}
			}
			if (radius == safety) {
				// Error! Something went wrong.
				plugin.getLogger().info("safety exceeded in search()");
				return;
			}
		}
	}

	private boolean isCleanCircle() {
		// Is the circle clean and untarnished?
		// (i.e. no redstone touching on inner/outer sides)
		return getShell(radius + 1) == Shell.EMPTY
			&& getShell(radius - 1) == Shell.EMPTY;
	}

	// Determines the type of a given shell of radius `r`
	private Shell getShell(int r) {
		int numEdgeBlocks = 8*r;

		// Traversal state.
		int chalk = 0;
		XZ pos	= new XZ(r, 0);
		XZ step	= new XZ(0, 1);

		// Traverse along edge.
		for (int steps = 1; steps <= numEdgeBlocks; steps++) {
			if (isChalk(getBlockAt(pos))) {
				chalk++;
			}
			// Break early if any chalk is missing.
			if (chalk > 0 && chalk < steps) {
				break;
			}
			// Rotate stepper if corner detected.
			if (Math.abs(pos.x) == Math.abs(pos.z)) {
				step.rotateCCW();
			}
			pos.x += step.x;
			pos.z += step.z;
		}

		if (chalk == 0) {
			return Shell.EMPTY;
		}
		if (chalk == numEdgeBlocks) {
			return Shell.SOLID;
		}
		for(XZ edge = new XZ(-r, -r); edge.x <= r; edge.x++) {
			if (!isChalk(getBlockAt(edge))) {
				return Shell.MIXED;
			}
		}
		return Shell.FLUSH_TO_EDGE;
	}

	private int getGlobalX(XZ v) {
		return block.getX() + (radius + v.z)*dir.x + v.x*dir.z;
	}
	private int getGlobalZ(XZ v) {
		return block.getZ() + (radius + v.z)*dir.z - v.x*dir.x;
	}

	private Block getBlockAt(XZ p) {
		return block.getWorld()
			.getBlockAt(getGlobalX(p), block.getY(), getGlobalZ(p));
	}
}