/**
 * 
 */
package org.morganm.activitytracker;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event;

/** Lightweight non-OOP object for performance tracking of block changes.
 * 
 * @author morganm
 *
 */
public class BlockChange {
	// should only be BLOCK_PLACE or BLOCK_BREAK
	public Event.Type eventType;
	
	public World world;
	public int x;
	public int y;
	public int z;
	
	public Material type;
	public byte data;
}
