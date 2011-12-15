/**
 * 
 */
package org.morganm.activitytracker;

import java.util.HashSet;
import java.util.List;

/** Class to keep track of which players are being tracked.
 * 
 * @author morganm
 *
 */
public class TrackerManager {
	private ActivityTracker plugin;
	private PermissionWrapper permHandler;
	private final HashSet<String> trackedPlayers = new HashSet<String>(10);
	private List<String> trackedPermissions;
	
	public TrackerManager(ActivityTracker plugin) {
		this.plugin = plugin;
		this.permHandler = this.plugin.getPerm();
		loadConfig();
	}
	
	@SuppressWarnings("unchecked")
	public void loadConfig() {
		if( plugin.getConfig().get("trackedPermissions") != null ) {
			trackedPermissions = plugin.getConfig().getStringList("trackedPermissions");
		}
		else
			trackedPermissions = null;
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
	
	public void playerLogout(String playerName) {
		trackedPlayers.remove(playerName);
	}
	
	private boolean shouldBeTracked(String playerName) {
		if( trackedPermissions != null ) {
			for(String perm : trackedPermissions) {
				if( permHandler.has(playerName, perm) )
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isTracked(String playerName) {
		return trackedPlayers.contains(playerName);
	}
}
