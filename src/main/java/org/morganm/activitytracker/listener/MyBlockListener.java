/**
 * 
 */
package org.morganm.activitytracker.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.morganm.activitytracker.ActivityTracker;
import org.morganm.activitytracker.TrackerManager;
import org.morganm.activitytracker.block.BlockChange;
import org.morganm.activitytracker.block.BlockTracker;

import com.andune.minecraft.commonlib.Debug;

/**
 * @author morganm
 *
 */
public class MyBlockListener implements Listener {
	private final ActivityTracker plugin;
	private final BlockTracker tracker;
	private final TrackerManager trackerManager;
	private final Debug debug;
	
	public MyBlockListener(ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerManager = this.plugin.getTrackerManager();
		this.tracker = this.plugin.getBlockTracker();
		this.debug = plugin.getDebug();
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		
		Block b = event.getBlock();
		BlockChange bc = tracker.getEndObject();

		bc.playerName = event.getPlayer().getName();
		bc.time = System.currentTimeMillis();
	    bc.eventType = BlockChange.Type.BLOCK_BREAK;
		bc.x = b.getX();
		bc.y = b.getY();
		bc.z = b.getZ();
		bc.world = b.getWorld();
		bc.type = b.getType();
		bc.data = b.getData();
		bc.signData = null;
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		
		Block b = event.getBlock();
		BlockChange bc = tracker.getEndObject();
		
		bc.playerName = event.getPlayer().getName();
		bc.time = System.currentTimeMillis();
	    bc.eventType = BlockChange.Type.BLOCK_PLACE;
		bc.x = b.getX();
		bc.y = b.getY();
		bc.z = b.getZ();
		bc.world = b.getWorld();
		bc.type = b.getType();
		bc.data = b.getData();
		
		bc.signData = null;
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onSignChange(SignChangeEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		
		Block b = event.getBlock();
		BlockChange bc = tracker.getEndObject();
		
		bc.playerName = event.getPlayer().getName();
		bc.time = System.currentTimeMillis();
	    bc.eventType = BlockChange.Type.SIGN_CHANGE;
		bc.x = b.getX();
		bc.y = b.getY();
		bc.z = b.getZ();
		bc.world = b.getWorld();
		bc.type = b.getType();
		bc.data = b.getData();
		
		// should always be true, this is a SIGN_CHANGE event, after all..
		if( bc.type == Material.SIGN || bc.type == Material.SIGN_POST ) {
			debug.debug("onSignChange: sign placed");
			BlockState bs = b.getState();
			if( bs instanceof Sign ) {
				debug.debug("onSignChange: recording sign data");
				Sign sign = (Sign) bs;
				bc.signData = sign.getLines();
			}
			else
				bc.signData = null;
		}
		else
			bc.signData = null;
	}
}
