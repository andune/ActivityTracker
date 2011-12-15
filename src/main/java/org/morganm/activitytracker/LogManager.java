/**
 * 
 */
package org.morganm.activitytracker;

import java.util.HashMap;

/** Class to manage log files, one per player.
 * 
 * @author morganm
 *
 */
public class LogManager {
	private ActivityTracker plugin;
	private HashMap<String, Log> logs = new HashMap<String, Log>(10);
	
	public LogManager(ActivityTracker plugin) {
		this.plugin = plugin;
	}
	
	public Log getLog(String playerName) {
		Log log = logs.get(playerName);
		if( log == null ) {
			log = new Log(plugin, playerName);
			log.init();
			logs.put(playerName,  log);
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
}
