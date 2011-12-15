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
	private final HashSet<String> trackedPlayers = new HashSet<String>(10);
	
	public TrackerManager(ActivityTracker plugin) {
		this.plugin = plugin;
	}
	
	/** Called when a player logs in to give this class the oportunity to determine whether
	 * it should start tracking them or not.
	 * 
	 * @param playerName
	 */
	public void playerLogin(String playerName) {
		if( shouldBeTracked(playerName) )
			trackedPlayers.add(playerName);
	}
	
	private boolean shouldBeTracked(String playerName) {
		// TODO
		return true;
	}
	
	public boolean isTracked(String playerName) {
		return trackedPlayers.contains(playerName);
	}
}
