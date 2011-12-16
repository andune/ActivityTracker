/**
 * 
 */
package org.morganm.activitytracker.util;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author morganm
 *
 */
public class General {
	private static General instance;
	
	private General() {}
	
	public static General getInstance() {
		if( instance == null )
			instance = new General();
		return instance;
	}
	
	public String shortLocationString(Location l) {
		if( l == null )
			return "null";
		else {
			World w = l.getWorld();
			String worldName = null;
			if( w != null )
				worldName = w.getName();
			else
				worldName = "(world deleted)";
			return worldName+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
		}
	}
}
