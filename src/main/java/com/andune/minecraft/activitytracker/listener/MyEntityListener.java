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

import java.io.File;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.andune.minecraft.activitytracker.ActivityTracker;
import com.andune.minecraft.activitytracker.Log;
import com.andune.minecraft.activitytracker.LogManager;
import com.andune.minecraft.activitytracker.TrackerManager;
import com.andune.minecraft.commonlib.GeneralBukkit;

/**
 * @author andune
 *
 */
public class MyEntityListener implements Listener {
	private final ActivityTracker plugin;
	private final TrackerManager trackerManager;
	private final LogManager logManager;
	private final GeneralBukkit util;
	private Log deathItemLog;
	
	public MyEntityListener(ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerManager = this.plugin.getTrackerManager();
		this.logManager = this.plugin.getLogManager();
		this.util = new GeneralBukkit();
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity e = event.getEntity();
		Entity killerE = null;
		
		EntityDamageEvent lastDamageEvent = e.getLastDamageCause();
		if( lastDamageEvent instanceof EntityDamageByEntityEvent ) {
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) lastDamageEvent;
			killerE = edbee.getEntity();
		}
		
		Player p = null;
		Player killerPlayer = null;
		if( e instanceof Player )
			p = (Player) e;
		if( killerE instanceof Player )
			killerPlayer = (Player) killerE;
		// either the entity being killed or the killer must be a player for us to care
		if( p == null && killerPlayer == null )
			return;
		
		// are we tracking player death items?  If so, log the items they have.
		if( plugin.isDeathItemLogEnabled() && p != null ) {
			if( deathItemLog == null )
				initDeathItemLog();
			
			final String deathLocationString = util.shortLocationString(p.getLocation()); 
			final PlayerInventory inv = p.getInventory();
			
			ItemStack[] items = inv.getContents();
			for(int i=0; i < items.length; i++) {
				if( items[i] != null )
					deathItemLog.logMessage("player " + p.getName() + " died "
							+ " at location " + deathLocationString
							+ " with item in inventory: "+items[i]);
			}
			
			ItemStack[] armor = inv.getArmorContents();
			for(int i=0; i < armor.length; i++) {
				if( armor[i] != null && armor[i].getTypeId() != 0 )
					deathItemLog.logMessage("player " + p.getName() + " died "
							+ " at location " + deathLocationString
							+ " wearing armor: "+armor[i]);
			}
			
		}
		
		if( !trackerManager.isTracked(p) && !trackerManager.isTracked(killerPlayer) )
			return;
		
		// this player died
		if( p != null ) {
			String playerName = p.getName();
			
			Log log = logManager.getLog(playerName);
			log.logMessage("player "+playerName+" died at location "+util.shortLocationString(p.getLocation())+", killer="+killerE);
		}
		// this player did the killing
		else if( killerPlayer != null ) {
			String playerName = killerPlayer.getName();
			
			Log log = logManager.getLog(playerName);
			log.logMessage("player "+playerName+" killed "+e+" at location "+util.shortLocationString(e.getLocation()));
			
		}
	}
	
	private void initDeathItemLog() {
		final String logDir = plugin.getConfig().getString("logDir");
		deathItemLog = new Log(plugin, new File(logDir+"/deathItems.log"));
	}
}
