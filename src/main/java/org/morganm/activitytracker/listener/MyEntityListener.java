/**
 * 
 */
package org.morganm.activitytracker.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.morganm.activitytracker.ActivityTracker;
import org.morganm.activitytracker.Log;
import org.morganm.activitytracker.LogManager;
import org.morganm.activitytracker.TrackerManager;
import org.morganm.activitytracker.util.General;

/**
 * @author morganm
 *
 */
public class MyEntityListener implements Listener {
	private final ActivityTracker plugin;
	private final TrackerManager trackerManager;
	private final LogManager logManager;
	private final General util;
	
	public MyEntityListener(ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerManager = this.plugin.getTrackerManager();
		this.logManager = this.plugin.getLogManager();
		this.util = General.getInstance();
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity e = event.getEntity();
		Entity killerE = null;
		
		EntityDamageEvent lastDamageEvent = e.getLastDamageCause();
		if( lastDamageEvent instanceof EntityDamageByEntityEvent ) {
			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) lastDamageEvent;
			killerE = edbee.getEntity();
		}
		
		Player p = null;
		Player killerPlayer = null;
		if( e instanceof Player )
			p = (Player) e;
		if( killerE instanceof Player )
			killerPlayer = (Player) killerE;
		// either the entity being killed or the killer must be a player for us to care
		if( p == null && killerPlayer == null )
			return;
		
		if( !trackerManager.isTracked(p) && !trackerManager.isTracked(killerPlayer) )
			return;
		
		// this player died
		if( p != null ) {
			String playerName = p.getName();
			
			Log log = logManager.getLog(playerName);
			log.logMessage("player died at location "+util.shortLocationString(p.getLocation())+", killer="+killerE);
		}
		// this player did the killing
		else if( killerPlayer != null ) {
			String playerName = killerPlayer.getName();
			
			Log log = logManager.getLog(playerName);
			log.logMessage("player killed "+e+" at location "+util.shortLocationString(e.getLocation()));
			
		}
	}
}
