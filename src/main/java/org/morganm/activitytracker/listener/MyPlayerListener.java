/**
 * 
 */
package org.morganm.activitytracker.listener;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.PlayerInventory;
import org.morganm.activitytracker.ActivityTracker;
import org.morganm.activitytracker.Log;
import org.morganm.activitytracker.LogManager;
import org.morganm.activitytracker.TrackerManager;
import org.morganm.activitytracker.util.Debug;
import org.morganm.activitytracker.util.General;
import org.morganm.activitytracker.util.PlayerUtil;

/**
 * @author morganm
 *
 */
public class MyPlayerListener extends PlayerListener {
	private final HashSet<Integer> leftClickRecord = new HashSet<Integer>(20);
	private ActivityTracker plugin;
	private TrackerManager trackerManager;
	private LogManager logManager;
	private Debug debug;
	private General util;
	private HashMap<String, String> banReasons = new HashMap<String, String>(20);
	
	public MyPlayerListener(ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerManager = this.plugin.getTrackerManager();
		this.logManager = this.plugin.getLogManager();
		this.debug = Debug.getInstance();
		this.util = General.getInstance();
		
		initializeLeftClickRecordMap();
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		debug.debug("onPlayerJoin: event=",event);
		
		String playerName = event.getPlayer().getName();
		trackerManager.playerLogin(event.getPlayer());
		
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		
		boolean newPlayer = PlayerUtil.getInstance().isNewPlayer(playerName);
		Log log = logManager.getLog(playerName);
		log.logMessage("player logged in" + (newPlayer ? " (NEW PLAYER)" : ""));
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		plugin.getTrackerManager().playerLogout(event.getPlayer());
		plugin.getMovementTracker().playerLogout(event.getPlayer());
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player quit");
		logManager.closeLog(playerName);
	}
	
	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		plugin.getTrackerManager().playerLogout(event.getPlayer());
		plugin.getMovementTracker().playerLogout(event.getPlayer());
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player kicked, reason: "+event.getReason()+" (banReason: "+banReasons.get(playerName)+")");
		banReasons.remove(playerName);
		logManager.closeLog(playerName);
	}
	
	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		// we don't care about permissions checks, we're just capturing the reason
		// for future use. If the person can't actually kick/ban, then this won't
		// ever get used so it doesn't matter.
		if( event.getMessage().startsWith("/ban") ) {
			String msg = event.getMessage();
			String[] parts = msg.split(" ");
			if( parts.length > 1 ) {
				banReasons.put(parts[1], msg);
			}
		}
		
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player command (pre-process): "+event.getMessage());
	}
	
	@Override
	public void onItemHeldChange(PlayerItemHeldEvent event) {
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
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
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("emptied bucket "+event.getBucket()+" at block "
				+util.shortLocationString(event.getBlockClicked().getLocation()));
	}
	
	@Override
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("filled bucket "+event.getBucket()+" at block "
				+util.shortLocationString(event.getBlockClicked().getLocation())
				+", blockType="+event.getBlockClicked().getType());
	}
	
	@Override
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		String to = event.getPlayer().getWorld().getName();
		String from = "null";
		if( event.getFrom() != null )
			from = event.getFrom().getName();
		Log log = logManager.getLog(playerName);
		log.logMessage("changed world to "+to+" from "+from);
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		// we ignore isCancelled, since HeroChat, at least, sets this when it processes
		// a chat. So we just log every chat that comes through, cancelled or not.
//		if( event.isCancelled() )
//			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("chat: "+event.getMessage());
	}
	
	@Override
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player dropped item "+event.getItemDrop().getItemStack()+" at location "
				+util.shortLocationString(event.getPlayer().getLocation()));
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if( event.isCancelled() )
			return;
		// we ignore these since they will get picked up by onBlockPlace events
		if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.isBlockInHand() )
			return;
		if( event.getAction() == Action.LEFT_CLICK_BLOCK && !leftClickRecord.contains(Integer.valueOf(event.getClickedBlock().getTypeId())) )
			return;
		
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		StringBuilder sb = new StringBuilder();
		sb.append("playerInteract: action=");
		sb.append(event.getAction());
		sb.append(", eventType=");
		sb.append(event.getType());
		Block b = event.getClickedBlock();
		if( b != null ) {
			sb.append(", clickedBlock=");
			sb.append("l={");
			sb.append(util.shortLocationString(b.getLocation()));
			sb.append("},type=");
			sb.append(b.getType());
			sb.append(",data=");
			sb.append(b.getData());
		}
		sb.append(", itemInHand=");
		sb.append(event.getItem());
		
		Log log = logManager.getLog(playerName);
		log.logMessage(sb.toString());
	}
	
	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("playerInteract: entity="+event.getRightClicked()
				+", eventType="+event.getType()
				+", eventName="+(event.getType() == Type.CUSTOM_EVENT ? event.getEventName() : "null")
//				+", isCancelled="+event.isCancelled()
			);
	}
	
	@Override
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player picked up item "+event.getItem().getItemStack()+" at location "
				+ util.shortLocationString(event.getPlayer().getLocation()));
	}
	
	@Override
	public void onPlayerPortal(PlayerPortalEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("used portal to location "+util.shortLocationString(event.getTo())
				+" from location "+util.shortLocationString(event.getFrom()));
	}
	
	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player respawned at location "+util.shortLocationString(event.getRespawnLocation()));
	}
	
	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		String playerName = event.getPlayer().getName();
		
		Log log = logManager.getLog(playerName);
		log.logMessage("player teleported from location "+util.shortLocationString(event.getFrom())
				+" to location "+util.shortLocationString(event.getTo()));
	}
	
	private void initializeLeftClickRecordMap() {
		leftClickRecord.add(Integer.valueOf(54));		// chest
		leftClickRecord.add(Integer.valueOf(63));		// sign post
		leftClickRecord.add(Integer.valueOf(64));		// wooden door
		leftClickRecord.add(Integer.valueOf(68));		// wall sign
		leftClickRecord.add(Integer.valueOf(69));		// lever
		leftClickRecord.add(Integer.valueOf(71));		// iron door
		leftClickRecord.add(Integer.valueOf(77));		// stone button
		leftClickRecord.add(Integer.valueOf(92));		// cake block (do we care?)
		leftClickRecord.add(Integer.valueOf(95));		// locked chest
		leftClickRecord.add(Integer.valueOf(96));		// trapdoor
		leftClickRecord.add(Integer.valueOf(116));		// enchantment table
	}
}
