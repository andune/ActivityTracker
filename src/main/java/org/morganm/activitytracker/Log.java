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
	
	public Log(ActivityTracker plugin) {
		this.plugin = plugin;
	}
	
	public void init() {
		isInitialized = true;
		// TODO
	}
	public void close() {
		// TODO
	}
	
	public void logMessage(String message) {
		if( !isInitialized ) {
			// TODO: log some error
			return;
		}
		
		// TODO: write me
	}
}
