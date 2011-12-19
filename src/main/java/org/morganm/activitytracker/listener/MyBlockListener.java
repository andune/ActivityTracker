/**
 * 
 */
package org.morganm.activitytracker.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.morganm.activitytracker.ActivityTracker;
import org.morganm.activitytracker.TrackerManager;
import org.morganm.activitytracker.block.BlockChange;
import org.morganm.activitytracker.block.BlockTracker;
import org.morganm.activitytracker.util.Debug;

/**
 * @author morganm
 *
 */
public class MyBlockListener extends BlockListener {
	private final ActivityTracker plugin;
	private final BlockTracker tracker;
	private final TrackerManager trackerManager;
	private final Debug debug;
	
	public MyBlockListener(ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerManager = this.plugin.getTrackerManager();
		this.tracker = this.plugin.getBlockTracker();
		this.debug = Debug.getInstance();
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		
		Block b = event.getBlock();
		BlockChange bc = tracker.getEndObject();

		bc.playerName = event.getPlayer().getName();
		bc.time = System.currentTimeMillis();
	    bc.eventType = event.getType();
		bc.x = b.getX();
		bc.y = b.getY();
		bc.z = b.getZ();
		bc.world = b.getWorld();
		bc.type = b.getType();
		bc.data = b.getData();
		bc.signData = null;
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		
		Block b = event.getBlock();
		BlockChange bc = tracker.getEndObject();
		
		bc.playerName = event.getPlayer().getName();
		bc.time = System.currentTimeMillis();
	    bc.eventType = event.getType();
		bc.x = b.getX();
		bc.y = b.getY();
		bc.z = b.getZ();
		bc.world = b.getWorld();
		bc.type = b.getType();
		bc.data = b.getData();
		
		bc.signData = null;
	}
	
	@Override
	public void onSignChange(SignChangeEvent event) {
		if( event.isCancelled() )
			return;
		if( !trackerManager.isTracked(event.getPlayer()) )
			return;
		
		Block b = event.getBlock();
		BlockChange bc = tracker.getEndObject();
		
		bc.playerName = event.getPlayer().getName();
		bc.time = System.currentTimeMillis();
	    bc.eventType = event.getType();
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
