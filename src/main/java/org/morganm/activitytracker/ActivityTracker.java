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
	public static final Logger log = Logger.getLogger(ActivityTracker.class.toString());
	public static final String logPrefix = "[ActivityLogger] ";

	private String version;
	private TrackerManager trackerManager;
	private LogManager logManager;
	private BlockTracker blockTracker;
	private MyBlockListener blockListener;
	private MyPlayerListener playerListener;
	private MyEntityListener entityListener;
	
	@Override
	public void onEnable() {
		version = getDescription().getVersion();
		
		trackerManager = new TrackerManager(this);
		logManager = new LogManager(this);
		blockTracker = new BlockTracker(this);
		
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
		
		// event type exists, but no Bukkit method call exists for this event yet
//		pm.registerEvent(Type.PLAYER_ITEM_HELD, playerListener, Priority.Monitor, this);
		
		entityListener = new MyEntityListener(this);
		pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Monitor, this);
		
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new MovementTracker(this), 100, 100);	// every 5 seconds
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new BlockLogger(this), 100, 100);		// every 5 seconds
		
		log.info(logPrefix + "version "+version+" is enabled");
	}

	@Override
	public void onDisable() {
		log.info(logPrefix + "version "+version+" is disabled");
	}

	public TrackerManager getTrackerManager() { return trackerManager; }
	public LogManager getLogManager() { return logManager; }
	public BlockTracker getBlockTracker() { return blockTracker; }
}
