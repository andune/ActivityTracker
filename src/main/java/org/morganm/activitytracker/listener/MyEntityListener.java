/**
 * 
 */
package org.morganm.activitytracker.listener;

import java.io.File;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.morganm.activitytracker.ActivityTracker;
import org.morganm.activitytracker.Log;
import org.morganm.activitytracker.LogManager;
import org.morganm.activitytracker.TrackerManager;

import com.andune.minecraft.commonlib.GeneralBukkit;

/**
 * @author morganm
 *
 */
public class MyEntityListener implements Listener {
	private final ActivityTracker plugin;
	private final TrackerManager trackerManager;
	private final LogManager logManager;
	private final GeneralBukkit util;
	private Log deathItemLog;
	
	public MyEntityListener(ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerManager = this.plugin.getTrackerManager();
		this.logManager = this.plugin.getLogManager();
		this.util = new GeneralBukkit();
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
		
		// are we tracking player death items?  If so, log the items they have.
		if( plugin.isDeathItemLogEnabled() && p != null ) {
			if( deathItemLog == null )
				initDeathItemLog();
			
			final String deathLocationString = util.shortLocationString(p.getLocation()); 
			final PlayerInventory inv = p.getInventory();
			
			ItemStack[] items = inv.getContents();
			for(int i=0; i < items.length; i++) {
				if( items[i] != null )
					deathItemLog.logMessage("player " + p.getName() + " died "
							+ " at location " + deathLocationString
							+ " with item in inventory: "+items[i]);
			}
			
			ItemStack[] armor = inv.getArmorContents();
			for(int i=0; i < armor.length; i++) {
				if( armor[i] != null && armor[i].getTypeId() != 0 )
					deathItemLog.logMessage("player " + p.getName() + " died "
							+ " at location " + deathLocationString
							+ " wearing armor: "+armor[i]);
			}
			
		}
		
		if( !trackerManager.isTracked(p) && !trackerManager.isTracked(killerPlayer) )
			return;
		
		// this player died
		if( p != null ) {
			String playerName = p.getName();
			
			Log log = logManager.getLog(playerName);
			log.logMessage("player "+playerName+" died at location "+util.shortLocationString(p.getLocation())+", killer="+killerE);
		}
		// this player did the killing
		else if( killerPlayer != null ) {
			String playerName = killerPlayer.getName();
			
			Log log = logManager.getLog(playerName);
			log.logMessage("player "+playerName+" killed "+e+" at location "+util.shortLocationString(e.getLocation()));
			
		}
	}
	
	private void initDeathItemLog() {
		final String logDir = plugin.getConfig().getString("logDir");
		deathItemLog = new Log(plugin, new File(logDir+"/deathItems.log"));
	}
}
