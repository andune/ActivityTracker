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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import com.andune.minecraft.commonlib.Debug;
import com.andune.minecraft.commonlib.GeneralBukkit;

/** Rather than hooking the expensive PLAYER_MOVE and forcing the Bukkit overhead associated with
 * producing and processing those events on every PLAYER_MOVE, we instead schedule a process to
 * check player locations of currently tracked players, which is a much smaller list than every
 * player and therefore a lot less processing. Further, because we aren't changing anything, we
 * can do this asynchronously so it can run on a separate thread/processor than the main thread.
 * 
 * @author andune
 *
 */
public class MovementTracker implements Runnable {
	private final ActivityTracker plugin;
	private final LogManager logManager;
	private final HashMap<HumanEntity,Location> positions = new HashMap<HumanEntity,Location>(10);
	private final Debug debug;
	private final GeneralBukkit util;
	private final TrackerManager trackerMgr;
	private int taskId;
	
	private final DecimalFormat df = new DecimalFormat("#.#");
	
	public MovementTracker(final ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerMgr = this.plugin.getTrackerManager();
		this.logManager = this.plugin.getLogManager();
		this.debug = plugin.getDebug();
		this.util = new GeneralBukkit();
		debug.debug("MovementTracker constructur: trackerMgr=",trackerMgr);
}
	
	public void setTaskId(int id) { this.taskId = id; }
	public int getTaskId() { return taskId; }
	
	public void playerLogout(Player p) {
		positions.remove(p);
	}
	
	public void run() {
		Set<HumanEntity> players = trackerMgr.getTrackedPlayers();
		for(HumanEntity p : players) {
			Location curPos = p.getLocation();
			Location prevPos = positions.get(p);
			if( prevPos == null ) {
				positions.put(p, curPos);
				continue;
			}
			positions.put(p, curPos);
			
			debug.debug("MovementTracker.run(): processing movement for player ",p);
			
			String curWorld = curPos.getWorld().getName();
			String prevWorld = prevPos.getWorld().getName();
			Log log = logManager.getLog(p.getName());
			if( !curWorld.equals(prevWorld)
					|| curPos.getBlockX() != prevPos.getBlockX()
					|| curPos.getBlockY() != prevPos.getBlockY()
					|| curPos.getBlockZ() != prevPos.getBlockZ() ) {
				String distanceString = null;
				if( curWorld.equals(prevWorld) )
					distanceString = df.format(curPos.distance(prevPos));
				else
					distanceString = "(crossworld)";
				
				log.logMessage("player moved distance "
						+ distanceString
						+", curLoc="+util.shortLocationString(curPos)
						+", prevLoc="+util.shortLocationString(prevPos));
			}
		}
	}
}