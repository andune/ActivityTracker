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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.andune.minecraft.activitytracker.block.BlockLogger;
import com.andune.minecraft.activitytracker.block.BlockTracker;
import com.andune.minecraft.activitytracker.listener.MyBlockListener;
import com.andune.minecraft.activitytracker.listener.MyEntityListener;
import com.andune.minecraft.activitytracker.listener.MyInventoryListener;
import com.andune.minecraft.activitytracker.listener.MyPlayerListener;
import com.andune.minecraft.commonlib.Debug;
import com.andune.minecraft.commonlib.JarUtils;
import com.andune.minecraft.commonlib.PermissionSystem;

/**
 * @author andune
 *
 */
public class ActivityTracker extends JavaPlugin {
	public static final Logger log = Logger.getLogger(ActivityTracker.class.toString());
	public static final String logPrefix = "[ActivityTracker] ";

	private String version;
	private int buildNumber = -1;
	private boolean configLoaded = false;
	
	private PermissionSystem perm;
	private JarUtils jarUtil;
	private Debug debug;
	
	private TrackerManager trackerManager;
	private LogManager logManager;
	private BlockTracker blockTracker;		// circular buffer to track broken blocks
	private MovementTracker movementTracker;// movement tracker, called on a schedule to track movement
	private BlockLogger blockLogger;		// runnable called on a schedule to log block breaks
	private Commands commandProcessor;
	
	@Override
	public void onEnable() {
	    debug = new Debug(this, this.getLogger());
	    
		version = getDescription().getVersion();
		jarUtil = new JarUtils(getDataFolder(), getFile());
		buildNumber = jarUtil.getBuildNumber();
		
		loadConfig();
		
//		System.out.println("bukkit version = "+Bukkit.getBukkitVersion());
		
		perm = new PermissionSystem(this, log);
		perm.setupPermissions();
		
		trackerManager = new TrackerManager(this);
		logManager = new LogManager(this);
		blockTracker = new BlockTracker(this);
		commandProcessor = new Commands(this);
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new MyBlockListener(this), this);
		pm.registerEvents(new MyPlayerListener(this), this);
		pm.registerEvents(new MyEntityListener(this), this);
		pm.registerEvents(new MyInventoryListener(this), this);
		
//		if (pm.isPluginEnabled("Spout")) {
//			pm.registerEvent(Type.CUSTOM_EVENT, new MySpoutChestAccessListener(this), Priority.Monitor, this);
//			log.info(logPrefix+ "Using Spout API to log chest access");
//		}
		
		blockLogger = new BlockLogger(this);
		getServer().getScheduler().runTaskTimerAsynchronously(this, blockLogger, 100, 100);       // every 5 seconds
		
		postConfig();
		
		// cover the case where we are reloaded live, by making sure all online
		// players (if any) go through the tracker check
		Player[] players = getServer().getOnlinePlayers();
		for(int i=0; i < players.length; i++)
			trackerManager.playerLogin(players[i]);
		
		log.info(logPrefix + "version "+version+", build "+buildNumber+" is enabled");
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);

		logManager.closeAll();
		blockLogger.cancel();
		log.info(logPrefix + "version "+version+", build "+buildNumber+" is disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		return commandProcessor.onCommand(sender, command, label, args);
	}

	public void loadConfig() {
		File file = new File(getDataFolder(), "config.yml");
		if( !file.exists() ) {
		    try {
		        jarUtil.copyConfigFromJar("config.yml", file);
		    }
		    catch(IOException e) {
		        log.log(Level.WARNING, e.getMessage(), e);
		    }
		}
		
		if( !configLoaded ) {
			super.getConfig();
			configLoaded = true;
		}
		else
			super.reloadConfig();
		
		debug.setDebug(getConfig().getBoolean("devDebug", false), Level.FINEST);
		debug.setDebug(getConfig().getBoolean("debug", false));
	}
	
	public void liveReloadConfig() {
		super.reloadConfig();
		postConfig();
	}

	/** Things to do after the config has been loaded/reloaded.
	 */
	public void postConfig() {
		// cancel old movementTracker job, if any
		if( movementTracker != null )
			getServer().getScheduler().cancelTask(movementTracker.getTaskId());
		
		if( getConfig().getBoolean("logMovement") ) {
			int seconds = getConfig().getInt("logMovementInterval");
			if( movementTracker == null )
				movementTracker = new MovementTracker(this);
			BukkitTask task = getServer().getScheduler().runTaskTimerAsynchronously(this, movementTracker, seconds*20, seconds*20);
			movementTracker.setTaskId(task.getTaskId());
		}
	}
	
	public boolean isPickupDropLogEnabled() {
		return getConfig().getBoolean("allPickupDropLogging", false);
	}
	
	public boolean isDeathItemLogEnabled() {
		return getConfig().getBoolean("deathItemLogging", false);
	}
	
	public Debug getDebug() { return debug; }
	
	public TrackerManager getTrackerManager() { return trackerManager; }
	public LogManager getLogManager() { return logManager; }
	public BlockTracker getBlockTracker() { return blockTracker; }
	public PermissionSystem getPerm() { return perm; }
	public MovementTracker getMovementTracker() { return movementTracker; }
	
}
