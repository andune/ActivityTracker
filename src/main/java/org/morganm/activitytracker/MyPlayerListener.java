/**
 * 
 */
package org.morganm.activitytracker;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author morganm
 *
 */
public class MyPlayerListener extends PlayerListener {
	private ActivityTracker plugin;
	private TrackerManager trackerManager;
	private LogManager logManager;
	
	public MyPlayerListener(ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerManager = this.plugin.getTrackerManager();
		this.logManager = this.plugin.getLogManager();
	}
	
	@Override
	public void onItemHeldChange(PlayerItemHeldEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Player p = event.getPlayer();
		PlayerInventory inventory = p.getInventory();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("changed held item to slot "
				+event.getNewSlot()
				+", item "+inventory.getItem(event.getNewSlot())
				+" (old slot was "+event.getPreviousSlot()
				+", item "+inventory.getItem(event.getPreviousSlot())
			);
	}
	
	@Override
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("emptied bucket "+event.getBucket()+" at block "+event.getBlockClicked().getLocation());
	}
}
