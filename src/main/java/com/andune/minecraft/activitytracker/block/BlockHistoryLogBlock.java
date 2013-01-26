/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013 Andune (andune.alleria@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
/**
 * 
 */
package com.andune.minecraft.activitytracker.block;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.andune.minecraft.activitytracker.ActivityTracker;
import com.andune.minecraft.commonlib.Debug;

import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;

/**
 * @author andune
 *
 */
public class BlockHistoryLogBlock implements BlockHistoryManager {
	private final JavaPlugin plugin;
	private final LogBlock logBlock;
	private final Debug debug;
	private final BlockHistoryCache bhCache;
	
	public BlockHistoryLogBlock(final ActivityTracker plugin, final BlockHistoryCache bhCache) {
		this.plugin = plugin;
		this.debug = plugin.getDebug();
		this.bhCache = bhCache;
		
		Plugin p = this.plugin.getServer().getPluginManager().getPlugin("LogBlock");
		if( p instanceof LogBlock )
			this.logBlock = (LogBlock) p;
		else
			this.logBlock = null;
	}
	
	@Override
	public BlockHistory getBlockHistory(final Location l) {
		if( l == null )
			return null;
		
		// check the cache to see if we already have the history for this location
		BlockHistory bh = bhCache.getCacheObject(l);
		if( bh != null )
			return bh;
		
		// if it's a broken block and we have logBlock, lookup the owner
		if( logBlock != null ) {
			debug.debug("running logBlock query");
			QueryParams params = new QueryParams(logBlock);
			params.bct = BlockChangeType.CREATED;
//			params.since = 43200;		// 30 days
			params.since = 107373;		// roughly 3 months
			params.loc = l;
			params.world = l.getWorld();
			params.silent = true;
//			params.needDate = true;
			params.needType = true;
			params.needPlayer = true;
			params.radius = 0;
			// order descending and limit 1, we just want the most recent blockChange
			params.limit = 1;
			params.order = QueryParams.Order.DESC;
			try {
				if( debug.isDevDebug() ) {
					debug.devDebug("logBlock query = ",params.getQuery());
				}
				for (de.diddiz.LogBlock.BlockChange lbChange : logBlock.getBlockChanges(params)) {
					bh = new BlockHistory(lbChange.playerName, lbChange.type, l);
					debug.debug("got logBlock result, lbOwner=",lbChange.playerName);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		// store it in the cache
		if( bh != null )
			bhCache.storeCacheObject(bh);
		
		return bh;
	}

}
