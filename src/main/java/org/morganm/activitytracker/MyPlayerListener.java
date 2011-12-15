/**
 * 
 */
package org.morganm.activitytracker;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
	public void onPlayerLogin(PlayerLoginEvent event) {
		String playerName = event.getPlayer().getName();
		trackerManager.playerLogin(playerName);
		
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player kicked");
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
	
	@Override
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("filled bucket "+event.getBucket()+" at block "+event.getBlockClicked().getLocation()+", blockType="+event.getBlockClicked().getType());
	}
	
	@Override
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		String to = event.getPlayer().getWorld().getName();
		String from = "null";
		if( event.getFrom() != null )
			from = event.getFrom().getName();
		Log log = logManager.getLog(playerName);
		log.logMessage("changed world to "+to+" from "+from);
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("chat: "+event.getMessage());
	}
	
	@Override
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player dropped item "+event.getItemDrop()+" at location "+event.getPlayer().getLocation());
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		// we ignore these since they will get picked up by onBlockPlace events
		if( event.isBlockInHand() )
			return;
		
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("playerInteract: action="+event.getAction()
				+", eventType="+event.getType()
				+", clickedBlock="+event.getClickedBlock()
				+", itemInHand="+event.getItem()
				+", isCancelled="+event.isCancelled()
			);
	}
	
	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("playerInteract: entity="+event.getRightClicked()
				+", eventType="+event.getType()
				+", eventName="+(event.getType() == Type.CUSTOM_EVENT ? event.getEventName() : "null")
				+", isCancelled="+event.isCancelled()
			);
	}
	
	@Override
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player picked up item "+event.getItem()+" at location "+event.getPlayer().getLocation());
	}
	
	@Override
	public void onPlayerPortal(PlayerPortalEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("used portal to location "+event.getTo()+" from location "+event.getFrom());
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player quit");
		
		logManager.closeLog(playerName);
		plugin.getTrackerManager().playerLogout(playerName);
	}
	
	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player kicked");
		
		logManager.closeLog(playerName);
		plugin.getTrackerManager().playerLogout(playerName);
	}
	
	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player respawned at location "+event.getRespawnLocation());
	}
	
	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		String playerName = event.getPlayer().getName();
		if( !trackerManager.isTracked(playerName) )
			return;
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player teleported from location "+event.getFrom()+" to location "+event.getTo());
	}
}
