/**
 * 
 */
package org.morganm.activitytracker;

/** Class that actually handles the logging activity.
 * 
 * @author morganm
 *
 */
public class Log {
	private ActivityTracker plugin;
	private boolean isInitialized;
	private String playerName;
	
	public Log(ActivityTracker plugin, String playerName) {
		this.plugin = plugin;
		this.playerName = playerName;
	}
	
	public void init() {
		isInitialized = true;
		// TODO
	}
	public void close() {
		// TODO
	}
	
	public void logMessage(long msgTime, String message) {
		if( !isInitialized ) {
			// TODO: log some error
			return;
		}
		
		// TODO: write me
	}
	public void logMessage(String message) {
		logMessage(System.currentTimeMillis(), message);
	}
}
