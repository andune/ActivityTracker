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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import com.andune.minecraft.activitytracker.ActivityTracker;
import com.andune.minecraft.activitytracker.Log;
import com.andune.minecraft.activitytracker.LogManager;
import com.andune.minecraft.activitytracker.TrackerManager;
import com.andune.minecraft.activitytracker.block.BlockHistory;
import com.andune.minecraft.activitytracker.block.BlockHistoryFactory;
import com.andune.minecraft.activitytracker.block.BlockHistoryManager;
import com.andune.minecraft.commonlib.GeneralBukkit;

/** Code originally copied from @Diddiz's LogBlock plugin.
 * 
 * @author andune, Diddiz (original Logblock code)
 *
 */
public class MyInventoryListener implements Listener {
	private final ActivityTracker plugin;
	private final TrackerManager trackerManager;
	private final LogManager logManager;
	private final GeneralBukkit util;
	private BlockHistoryManager blockHistoryManager;
	private final Map<Player, ItemStack[]> containers = new HashMap<Player, ItemStack[]>();

	public MyInventoryListener(final ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerManager = this.plugin.getTrackerManager();
		this.logManager = this.plugin.getLogManager();
		this.blockHistoryManager = BlockHistoryFactory.getBlockHistoryManager(plugin);
		this.util = new GeneralBukkit();
	}

	/** When an inventory is closed, check to see if we had a "before" snapshot recorded
	 * to compare against. If so, log the differences.
	 * 
	 * @author andune, Diddiz (original LogBlock code)
	 */
	@EventHandler(priority=EventPriority.MONITOR)
	public void onInventoryClose(InventoryCloseEvent event) {
		final HumanEntity entity = event.getPlayer();
		Player player = null;
		if( entity instanceof Player )
			player = (Player) entity;
		if( player == null )
			return;
		
		final Location l = entity.getLocation();
		if( !trackerManager.isTracked(entity) )
			return;

		final ItemStack[] before = containers.get(player);
		if (before != null) {
			Log log = logManager.getLog(player);
			
			BlockHistory bh = blockHistoryManager.getBlockHistory(l);
			String blockOwner = null;
			if( bh != null )
				blockOwner = bh.getOwner();
			String ownerString = null;
			
			if( player.getName().equals(blockOwner) )
				ownerString = "owner="+blockOwner;
			else
				ownerString = "owner="+blockOwner+" ** NOT BLOCK OWNER **";

			final ItemStack[] after = util.compressInventory(event.getInventory().getContents());
			final ItemStack[] diff = util.compareInventories(before, after);
			for (final ItemStack item : diff) {
				if( item.getAmount() < 0 )
					log.logMessage("item "+item+" removed from container at {"+util.shortLocationString(l)+"}, "+ownerString);
				else
					log.logMessage("item "+item+" added to container at {"+util.shortLocationString(l)+"}, "+ownerString);
			}
			containers.remove(player);
		}
	}

	/** Record the "before" inventory of the container in memory when the chest is opened,
	 * so that we can later compare before/after to see what has changed. -andune
	 * 
	 * Original Spout code ignored crafting events, hopefully since Bukkit has a separate
	 * CraftItemEvent, that will happen automatically for us now.
	 * 
	 * @author andune, Diddiz (original LogBlock code)
	 */
	@EventHandler(priority=EventPriority.MONITOR)
	public void onInventoryOpen(InventoryOpenEvent event) {
		final HumanEntity entity = event.getPlayer();
		Player player = null;
		if( entity instanceof Player )
			player = (Player) entity;
		if( player == null )
			return;
		
		containers.put(player, util.compressInventory(event.getInventory().getContents()));
	}
	
	/** Log crafting events as well.
	 * 
	 * @author andune
	 */
	@EventHandler(priority=EventPriority.MONITOR)
	public void onInventoryCraft(CraftItemEvent event) {
		final HumanEntity entity = event.getWhoClicked();
		Player player = null;
		if( entity instanceof Player )
			player = (Player) entity;
		if( player == null )
			return;

		if( !trackerManager.isTracked(player) )
			return;

		final Location l = player.getLocation();
			
		ItemStack[] contents = event.getInventory().getContents();
		if( contents != null ) {
			final Log log = logManager.getLog(player);
			for(ItemStack item : contents) {
				if( item != null )
					log.logMessage("crafted item "+item+" at location {"+util.shortLocationString(l)+"}");
			}
		}
	}
}
