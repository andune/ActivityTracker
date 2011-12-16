/**
 * 
 */
package org.morganm.activitytracker.block;

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
	
	public String playerName;
	public long time;		// time of the event
	
	public World world;
	public int x;
	public int y;
	public int z;
	
	public Material type;
	public byte data;
	
	public String[] signData;
	
	public String locationString() {
		return "{"+world.getName()+",x="+x+",y="+y+",z="+z+"}";
	}
}
