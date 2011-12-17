/**
 * 
 */
package org.morganm.activitytracker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.morganm.activitytracker.util.Debug;
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
	private Debug debug;
	
	public TrackerManager(ActivityTracker plugin) {
		this.plugin = plugin;
		this.permHandler = this.plugin.getPerm();
		loadConfig();
		debug = Debug.getInstance();
	}
	
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
		debug.debug("playerLogin(): ",p);
		if( shouldBeTracked(p) )
			trackedPlayers.add(p);
	}
	
	/** Manual way to specify a player to be tracked.
	 * 
	 * @param p
	 */
	public void trackPlayer(Player p) {
		trackedPlayers.add(p);
	}
	/** Manual way to specify a player to stop tracking.
	 * 
	 * @param p
	 */
	public void unTrackPlayer(Player p) {
		trackedPlayers.remove(p);
	}
	
	public void playerLogout(Player p) {
		trackedPlayers.remove(p);
	}
	
	private boolean shouldBeTracked(Player p) {
		boolean shouldBeTracked = false;
		
		if( trackedPermissions != null ) {
			for(String perm : trackedPermissions) {
				debug.debug("shouldBeTracked(): p=",p," checking perm ",perm);
				if( permHandler.has(p, perm) ) {
					shouldBeTracked = true;
					break;
				}
			}
		}
		
		debug.debug("shouldBeTracked(): p=",p," shouldBeTracked=",shouldBeTracked);
		return shouldBeTracked;
	}
	
	public boolean isTracked(Player p) {
		if( p == null )
			return false;
		
		boolean isTracked = trackedPlayers.contains(p);
		debug.debug("isTracked for player ",p," = ",isTracked);
		return isTracked;
	}
}
