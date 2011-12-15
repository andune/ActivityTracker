/**
 * 
 */
package org.morganm.activitytracker.listener;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.morganm.activitytracker.ActivityTracker;
import org.morganm.activitytracker.TrackerManager;
import org.morganm.activitytracker.block.BlockChange;
import org.morganm.activitytracker.block.BlockTracker;

/**
 * @author morganm
 *
 */
public class MyBlockListener extends BlockListener {
	private ActivityTracker plugin;
	private BlockTracker tracker;
	private TrackerManager trackerManager;
	
	public MyBlockListener(ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerManager = this.plugin.getTrackerManager();
		this.tracker = this.plugin.getBlockTracker();
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
	}
}
