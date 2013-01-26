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
package com.andune.minecraft.activitytracker.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.andune.minecraft.activitytracker.ActivityTracker;
import com.andune.minecraft.activitytracker.TrackerManager;
import com.andune.minecraft.activitytracker.block.BlockChange;
import com.andune.minecraft.activitytracker.block.BlockTracker;
import com.andune.minecraft.commonlib.Debug;

/**
 * @author andune
 *
 */
public class MyBlockListener implements Listener {
	private final ActivityTracker plugin;
	private final BlockTracker tracker;
	private final TrackerManager trackerManager;
	private final Debug debug;
	
	public MyBlockListener(ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerManager = this.plugin.getTrackerManager();
		this.tracker = this.plugin.getBlockTracker();
		this.debug = plugin.getDebug();
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		
		Block b = event.getBlock();
		BlockChange bc = tracker.getEndObject();

		bc.playerName = event.getPlayer().getName();
		bc.time = System.currentTimeMillis();
	    bc.eventType = BlockChange.Type.BLOCK_BREAK;
		bc.x = b.getX();
		bc.y = b.getY();
		bc.z = b.getZ();
		bc.world = b.getWorld();
		bc.type = b.getType();
		bc.data = b.getData();
		bc.signData = null;
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		
		Block b = event.getBlock();
		BlockChange bc = tracker.getEndObject();
		
		bc.playerName = event.getPlayer().getName();
		bc.time = System.currentTimeMillis();
	    bc.eventType = BlockChange.Type.BLOCK_PLACE;
		bc.x = b.getX();
		bc.y = b.getY();
		bc.z = b.getZ();
		bc.world = b.getWorld();
		bc.type = b.getType();
		bc.data = b.getData();
		
		bc.signData = null;
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onSignChange(SignChangeEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		
		Block b = event.getBlock();
		BlockChange bc = tracker.getEndObject();
		
		bc.playerName = event.getPlayer().getName();
		bc.time = System.currentTimeMillis();
	    bc.eventType = BlockChange.Type.SIGN_CHANGE;
		bc.x = b.getX();
		bc.y = b.getY();
		bc.z = b.getZ();
		bc.world = b.getWorld();
		bc.type = b.getType();
		bc.data = b.getData();
		
		// should always be true, this is a SIGN_CHANGE event, after all..
		if( bc.type == Material.SIGN || bc.type == Material.SIGN_POST ) {
			debug.debug("onSignChange: sign placed");
			BlockState bs = b.getState();
			if( bs instanceof Sign ) {
				debug.debug("onSignChange: recording sign data");
				Sign sign = (Sign) bs;
				bc.signData = sign.getLines();
			}
			else
				bc.signData = null;
		}
		else
			bc.signData = null;
	}
}
