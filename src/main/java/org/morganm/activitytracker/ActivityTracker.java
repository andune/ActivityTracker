/**
 * 
 */
package org.morganm.activitytracker;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.activitytracker.block.BlockLogger;
import org.morganm.activitytracker.block.BlockTracker;
import org.morganm.activitytracker.listener.MyBlockListener;
import org.morganm.activitytracker.listener.MyEntityListener;
import org.morganm.activitytracker.listener.MyPlayerListener;
import org.morganm.activitytracker.util.Debug;
import org.morganm.activitytracker.util.JarUtils;
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
	private boolean configLoaded = false;
	
	private PermissionSystem perm;
	private JarUtils jarUtil;
	
	private TrackerManager trackerManager;
	private LogManager logManager;
	private BlockTracker blockTracker;		// circular buffer to track broken blocks
	private MyBlockListener blockListener;	// block listener to push block breaks into buffer
	private MyPlayerListener playerListener;// player listener to log player activity
	private MyEntityListener entityListener;// entity listener to log player deaths
	private MovementTracker movementTracker;// movement tracker, called on a schedule to track movement
	private BlockLogger blockLogger;		// runnable called on a schedule to log block breaks
	private Commands commandProcessor;
	
	@Override
	public void onEnable() {
		version = getDescription().getVersion();
		jarUtil = new JarUtils(this, getFile(), log, logPrefix);
		buildNumber = jarUtil.getBuildNumber();
		
		loadConfig();
		
//		System.out.println("bukkit version = "+Bukkit.getBukkitVersion());
		
		perm = new PermissionSystem(this, log, logPrefix);
		perm.setupPermissions();
		
		trackerManager = new TrackerManager(this);
		logManager = new LogManager(this);
		blockTracker = new BlockTracker(this);
		commandProcessor = new Commands(this);
		
		PluginManager pm = getServer().getPluginManager();
		blockListener = new MyBlockListener(this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Monitor, this);
		pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Monitor, this);
		
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
		pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Monitor, this);
		
		// event type exists, but no Bukkit method call exists for this event yet
//		pm.registerEvent(Type.PLAYER_ITEM_HELD, playerListener, Priority.Monitor, this);
		
		entityListener = new MyEntityListener(this);
		pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Monitor, this);
		
		movementTracker = new MovementTracker(this);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, movementTracker, 100, 100);	// every 5 seconds
		blockLogger = new BlockLogger(this);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, blockLogger, 100, 100);		// every 5 seconds
		
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
			jarUtil.copyConfigFromJar("config.yml", file);
		}
		
		if( !configLoaded ) {
			super.getConfig();
			configLoaded = true;
		}
		else
			super.reloadConfig();
		
		Debug.getInstance().init(log, logPrefix, false);
		Debug.getInstance().setDebug(getConfig().getBoolean("devDebug", false), Level.FINEST);
		Debug.getInstance().setDebug(getConfig().getBoolean("debug", false));
}
	
	public TrackerManager getTrackerManager() { return trackerManager; }
	public LogManager getLogManager() { return logManager; }
	public BlockTracker getBlockTracker() { return blockTracker; }
	public PermissionSystem getPerm() { return perm; }
	public MovementTracker getMovementTracker() { return movementTracker; }
	
}
