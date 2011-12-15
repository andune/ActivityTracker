/**
 * 
 */
package org.morganm.activitytracker.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.morganm.activitytracker.ActivityTracker;
import org.morganm.activitytracker.Log;
import org.morganm.activitytracker.LogManager;
import org.morganm.activitytracker.TrackerManager;

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
		
		if( !trackerManager.isTracked(p) )
			return;
		String playerName = p.getName();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player died at location "+p.getLocation());
	}
}
