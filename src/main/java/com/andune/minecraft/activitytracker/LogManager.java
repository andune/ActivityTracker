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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

/** Class to manage log files, one per player.
 * 
 * @author andune
 *
 */
public class LogManager {
	private static final Logger logger = ActivityTracker.log;
	private static final String logPrefix = ActivityTracker.logPrefix;
	
	private ActivityTracker plugin;
	private HashMap<String, Log> logs = new HashMap<String, Log>(10);
	
	public LogManager(ActivityTracker plugin) {
		this.plugin = plugin;
	}
	
	public Log getLog(String playerName) {
		Log log = logs.get(playerName);
		if( log == null ) {
			log = new Log(plugin, playerName);
			try {
				log.init();
				logs.put(playerName,  log);
			}
			catch(IOException e) {
				logger.warning(logPrefix+"Error opening logfile for player "+playerName+": "+e.getMessage());
				e.printStackTrace();
			}
		}
		
		return log;
	}
	public Log getLog(Player player) {
		return getLog(player.getName());
	}
	
	/** Called to close a player log and therefore release resources, usually
	 * called after player logout.
	 * 
	 * @param playerName
	 */
	public void closeLog(String playerName) {
		Log log = logs.get(playerName);
		if( log != null ) {
			log.close();
			logs.remove(playerName);
		}
	}
	
	public void closeAll() {
		for(Entry<String, Log> e : logs.entrySet())
			e.getValue().close();
	}
}
