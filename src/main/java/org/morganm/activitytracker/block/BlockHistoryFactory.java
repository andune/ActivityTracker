/**
 * 
 */
package org.morganm.activitytracker.block;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author morganm
 *
 */
public class BlockHistoryFactory {
	private static final BlockHistoryCache cache = new BlockHistoryCache();
	
	public static BlockHistoryManager getBlockHistoryManager(final JavaPlugin plugin) {
		Plugin p = plugin.getServer().getPluginManager().getPlugin("LogBlock");
		if( p != null )
			return new BlockHistoryLogBlock(plugin, cache);
		
		return new BlockHistoryNoOp();
	}
	
	public static BlockHistoryCache getBlockHistoryCache() { return cache; }
}
