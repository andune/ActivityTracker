/**
 * 
 */
package org.morganm.activitytracker;

import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.activitytracker.block.BlockLogger;
import org.morganm.activitytracker.block.BlockTracker;
import org.morganm.activitytracker.listener.MyBlockListener;
import org.morganm.activitytracker.listener.MyEntityListener;
import org.morganm.activitytracker.listener.MyPlayerListener;
import org.morganm.activitytracker.util.PermissionSystem;

/**
 * @author morganm
 *
 */
public class ActivityTracker extends JavaPlugin {
	public static final Logger log = Logger.getLogger(ActivityTracker.class.toString());
	public static final String logPrefix = "[ActivityTracker] ";

	private String version;
	private int buildNumber = -1;
	private TrackerManager trackerManager;
	private LogManager logManager;
	private BlockTracker blockTracker;
	private MyBlockListener blockListener;
	private MyPlayerListener playerListener;
	private MyEntityListener entityListener;
	private MovementTracker movementTracker;
	private PermissionSystem perm;
	
	@Override
	public void onEnable() {
		version = getDescription().getVersion();
		buildNumber = getBuildNumber();
		
		perm = new PermissionSystem(this, log, logPrefix);
		perm.setupPermissions();
		
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
		
		movementTracker = new MovementTracker(this);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, movementTracker, 100, 100);	// every 5 seconds
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new BlockLogger(this), 100, 100);		// every 5 seconds
		
		log.info(logPrefix + "version "+version+", build "+buildNumber+" is enabled");
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelAllTasks();
		log.info(logPrefix + "version "+version+", build "+buildNumber+" is enabled");
	}

	public TrackerManager getTrackerManager() { return trackerManager; }
	public LogManager getLogManager() { return logManager; }
	public BlockTracker getBlockTracker() { return blockTracker; }
	public PermissionSystem getPerm() { return perm; }
	public MovementTracker getMovementTracker() { return movementTracker; }
	
    private int getBuildNumber() {
    	int buildNum = -1;
    	
        try {
        	JarFile jar = new JarFile(getFile());
        	
            JarEntry entry = jar.getJarEntry("build.number");
            InputStream is = jar.getInputStream(entry);
        	Properties props = new Properties();
        	props.load(is);
        	is.close();
        	Object o = props.get("build.number");
        	if( o instanceof Integer )
        		buildNum = ((Integer) o).intValue();
        	else if( o instanceof String )
        		buildNum = Integer.parseInt((String) o);
        } catch (Exception e) {
            log.warning(logPrefix + " Could not load build number from JAR");
        }
        
        return buildNum;
    }
}
