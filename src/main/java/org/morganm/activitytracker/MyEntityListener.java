/**
 * 
 */
package org.morganm.activitytracker;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

/**
 * @author morganm
 *
 */
public class MyEntityListener extends EntityListener {
	private ActivityTracker plugin;
	private TrackerManager trackerManager;
	private LogManager logManager;
	
	public MyEntityListener(ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerManager = this.plugin.getTrackerManager();
		this.logManager = this.plugin.getLogManager();
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Entity e = event.getEntity();
		if( !(e instanceof Player) )
			return;
		Player p = (Player) e;
		
		String playerName = p.getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player died at location "+p.getLocation());
	}
}
