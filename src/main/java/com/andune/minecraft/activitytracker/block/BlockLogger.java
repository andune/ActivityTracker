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

import java.util.logging.Logger;

import org.bukkit.Location;

import com.andune.minecraft.activitytracker.ActivityTracker;
import com.andune.minecraft.activitytracker.Log;
import com.andune.minecraft.activitytracker.LogManager;
import com.andune.minecraft.commonlib.Debug;

/**
 * @author andune
 *
 */
public class BlockLogger implements Runnable {
	private static final Logger logger = ActivityTracker.log;
	private static final String logPrefix = ActivityTracker.logPrefix;
	
	private final ActivityTracker plugin;
	private final BlockTracker tracker;
	private final LogManager logManager;
	private final Debug debug;
	private final BlockHistoryCache blockHistoryCache;
	private BlockHistoryManager blockHistoryManager;
	private boolean isCanceled = false;
	
	public BlockLogger(final ActivityTracker plugin) {
		this.plugin = plugin;
		this.tracker = this.plugin.getBlockTracker();
		this.logManager = this.plugin.getLogManager();
		this.blockHistoryManager = BlockHistoryFactory.getBlockHistoryManager(plugin);
		this.blockHistoryCache = BlockHistoryFactory.getBlockHistoryCache();
		this.debug = plugin.getDebug();
	}
	
	public void cancel() {
		isCanceled = true;
		this.blockHistoryManager = null;
	}

	public void run() {
		if( isCanceled )
			return;
		
		BlockChange bc = null;
		
		// run until the queue is empty
		while( (bc = tracker.getStartObject()) != null ) {
			debug.debug("BlockLogger.run(): queue has an object pending, processing");
			Log log = logManager.getLog(bc.playerName);
			Location l = bc.getLocation();
			
			if( bc.eventType == BlockChange.Type.BLOCK_BREAK ) {
				String blockOwner = "(none)";
				
				if( blockHistoryManager != null ) {
					BlockHistory bh = blockHistoryManager.getBlockHistory(l);
					
					// we only count owner if the block destroyed was the same type placed,
					// this avoids logging when people cut down trees that were previously
					// saplings planted by players, etc
					if( bh != null && bh.getTypeId() == bc.type.getId() )
						blockOwner = bh.getOwner();
				}
				
				String postMsg = "";
				if( "(none)".equals(blockOwner) )
					debug.debug("no block owner found");
				else if( !bc.playerName.equals(blockOwner) )
					postMsg = " ** NOT BLOCK OWNER **";
				
				log.logMessage(bc.time, "block broken at "
						+bc.locationString()
						+", blockType="+bc.type
						+", lbOwner="+blockOwner
						+", blockData="+bc.data
						+postMsg
					);
			}
			else if( bc.eventType == BlockChange.Type.BLOCK_PLACE ) {
				log.logMessage(bc.time, "block placed at "
						+bc.locationString()
						+", blockType="+bc.type
						+", blockData="+bc.data
					);

				// store the object in the cache, this saves us a lookup later and
				// also makes sure the cache doesn't contain stale data for this location
				BlockHistory bh = new BlockHistory(bc.playerName, bc.type.getId(), bc.getLocation());
				blockHistoryCache.storeCacheObject(bh);
			}
			else if( bc.eventType == BlockChange.Type.SIGN_CHANGE ) {
				// record sign data, if any
				StringBuilder signData = new StringBuilder();
				if( bc.signData != null ) {
					for(int i=0; i < bc.signData.length; i++) {
						if( signData.length() > 0 )
							signData.append("|");
						signData.append(bc.signData[i]);
					}
					signData.insert(0, ", Sign data: ");
				}
				
				log.logMessage(bc.time, "sign text changed at "
						+bc.locationString()
						+", blockType="+bc.type
						+", blockData="+bc.data
						+signData
					);
			}
			else {
				logger.warning(logPrefix+"ERROR: unknown eventType in BlockLogger: "+bc.eventType);
			}
		}
	}
}
