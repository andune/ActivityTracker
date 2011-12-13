/**
 * 
 */
package org.morganm.activitytracker;

import java.util.HashSet;

/** Class to keep track of which players are being tracked.
 * 
 * @author morganm
 *
 */
public class TrackerManager {
	private ActivityTracker plugin;
	private final HashSet<String> trackedPlayers = new HashSet<String>();
	
	public TrackerManager(ActivityTracker plugin) {
		this.plugin = plugin;
	}
	
	public boolean isTracked(String playerName) {
		return trackedPlayers.contains(playerName);
	}
}
