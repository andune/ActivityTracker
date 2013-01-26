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
package com.andune.minecraft.activitytracker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.HumanEntity;

import com.andune.minecraft.commonlib.Debug;
import com.andune.minecraft.commonlib.PermissionSystem;

/** Class to keep track of which players are being tracked.
 * 
 * @author andune
 *
 */
public class TrackerManager {
	private ActivityTracker plugin;
	private PermissionSystem permHandler;
	private final HashSet<HumanEntity> trackedPlayers = new HashSet<HumanEntity>(10);
	private List<String> trackedPermissions;
	private Debug debug;
	
	public TrackerManager(ActivityTracker plugin) {
		this.plugin = plugin;
		this.permHandler = this.plugin.getPerm();
		loadConfig();
		debug = plugin.getDebug();
	}
	
	public void loadConfig() {
		if( plugin.getConfig().get("trackedPermissions") != null ) {
			trackedPermissions = plugin.getConfig().getStringList("trackedPermissions");
		}
		else
			trackedPermissions = null;
	}
	
	public Set<HumanEntity> getTrackedPlayers() { return trackedPlayers; }
	
	/** Called when a player logs in to give this class the oportunity to determine whether
	 * it should start tracking them or not.
	 * 
	 * @param playerName
	 */
	public void playerLogin(HumanEntity p) {
		debug.debug("playerLogin(): ",p);
		if( shouldBeTracked(p) )
			trackedPlayers.add(p);
	}
	
	/** Manual way to specify a player to be tracked.
	 * 
	 * @param p
	 */
	public void trackPlayer(HumanEntity p) {
		trackedPlayers.add(p);
	}
	/** Manual way to specify a player to stop tracking.
	 * 
	 * @param p
	 */
	public void unTrackPlayer(HumanEntity p) {
		trackedPlayers.remove(p);
	}
	
	public void playerLogout(HumanEntity p) {
		trackedPlayers.remove(p);
	}
	
	private boolean shouldBeTracked(HumanEntity p) {
		boolean shouldBeTracked = false;
		
		if( trackedPermissions != null ) {
			for(String perm : trackedPermissions) {
				debug.debug("shouldBeTracked(): p=",p," checking perm ",perm);
				if( permHandler.has(p, perm) ) {
					shouldBeTracked = true;
					break;
				}
			}
		}
		
		debug.debug("shouldBeTracked(): p=",p," shouldBeTracked=",shouldBeTracked);
		return shouldBeTracked;
	}
	
	public boolean isTracked(HumanEntity p) {
		if( p == null )
			return false;
		
		boolean isTracked = trackedPlayers.contains(p);
		debug.debug("isTracked for player ",p," = ",isTracked);
		return isTracked;
	}
}
