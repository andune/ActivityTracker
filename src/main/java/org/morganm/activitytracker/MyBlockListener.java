/**
 * 
 */
package org.morganm.activitytracker;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author morganm
 *
 */
public class MyBlockListener extends BlockListener {
	private ActivityTracker plugin;
	private BlockTracker tracker;
	
	public MyBlockListener(ActivityTracker plugin) {
		this.plugin = plugin;
		this.tracker = this.plugin.getBlockTracker();
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
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
