/**
 * 
 */
package org.morganm.activitytracker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

/** Class to manage log files, one per player.
 * 
 * @author morganm
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
