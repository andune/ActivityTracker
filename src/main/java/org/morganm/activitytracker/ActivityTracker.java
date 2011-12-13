/**
 * 
 */
package org.morganm.activitytracker;

import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author morganm
 *
 */
public class ActivityTracker extends JavaPlugin {
	private static final Logger log = Logger.getLogger(ActivityTracker.class.toString());
	private static final String logPrefix = "[ActivityLogger] ";

	private String version;
	private TrackerManager trackerManager;
	private LogManager logManager;
	private MyBlockListener blockListener;
	private MyPlayerListener playerListener;
	
	@Override
	public void onEnable() {
		version = getDescription().getVersion();
		
		trackerManager = new TrackerManager(this);
		logManager = new LogManager(this);
		
		PluginManager pm = getServer().getPluginManager();
		blockListener = new MyBlockListener(this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Monitor, this);
		
		playerListener = new MyPlayerListener(this);
		pm.registerEvent(Type.PLAYER_BUCKET_FILL, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_BUCKET_EMPTY, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_CHANGED_WORLD, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_DROP_ITEM, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_PORTAL, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_KICK, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_ITEM_HELD, playerListener, Priority.Monitor, this);
		
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new MovementTracker(this), 200, 200);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new BlockLogger(this), 100, 100);
		
		log.info(logPrefix + "version "+version+" is enabled");
	}

	@Override
	public void onDisable() {
		log.info(logPrefix + "version "+version+" is disabled");
	}

	public TrackerManager getTrackerManager() { return trackerManager; }
	public LogManager getLogManager() { return logManager; }
}
