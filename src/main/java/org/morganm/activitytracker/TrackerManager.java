/**
 * 
 */
package org.morganm.activitytracker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.morganm.activitytracker.util.PermissionSystem;

/** Class to keep track of which players are being tracked.
 * 
 * @author morganm
 *
 */
public class TrackerManager {
	private ActivityTracker plugin;
	private PermissionSystem permHandler;
	private final HashSet<Player> trackedPlayers = new HashSet<Player>(10);
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
	
	public Set<Player> getTrackedPlayers() { return trackedPlayers; }
	
	/** Called when a player logs in to give this class the oportunity to determine whether
	 * it should start tracking them or not.
	 * 
	 * @param playerName
	 */
	public void playerLogin(Player p) {
		if( shouldBeTracked(p) )
			trackedPlayers.add(p);
	}
	
	public void playerLogout(String playerName) {
		trackedPlayers.remove(playerName);
	}
	
	private boolean shouldBeTracked(Player p) {
		if( trackedPermissions != null ) {
			for(String perm : trackedPermissions) {
				if( permHandler.has(p, perm) )
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isTracked(Player p) {
		return trackedPlayers.contains(p);
	}
}
