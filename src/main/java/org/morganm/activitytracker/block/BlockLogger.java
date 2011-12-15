/**
 * 
 */
package org.morganm.activitytracker.block;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.morganm.activitytracker.ActivityTracker;
import org.morganm.activitytracker.Log;
import org.morganm.activitytracker.LogManager;

import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;

/**
 * @author morganm
 *
 */
public class BlockLogger implements Runnable {
	private static final Logger logger = ActivityTracker.log;
	private static final String logPrefix = ActivityTracker.logPrefix;
	
	private ActivityTracker plugin;
	private BlockTracker tracker;
	private LogManager logManager;
	private LogBlock logBlock;
	
	public BlockLogger(ActivityTracker plugin) {
		this.plugin = plugin;
		this.tracker = this.plugin.getBlockTracker();
		this.logManager = this.plugin.getLogManager();
		
		Plugin p = this.plugin.getServer().getPluginManager().getPlugin("LogBlock");
		if( p instanceof LogBlock )
			this.logBlock = (LogBlock) p; 
	}

	public void run() {
		BlockChange bc = null;
		
		// run until the queue is empty
		while( (bc = tracker.getStartObject()) != null ) {
			Log log = logManager.getLog(bc.playerName);
			
			if( bc.eventType == Type.BLOCK_BREAK ) {
				String lbOwner = "(none)";
				
				// if it's a broken block and we have logBlock, lookup the owner
				if( logBlock != null ) {
					QueryParams params = new QueryParams(logBlock);
					params.bct = BlockChangeType.CREATED;
					params.since = 43200;		// is this in minutes?  seconds? 43200 = 30 days in minutes
					params.loc = new Location(bc.world, bc.x, bc.y, bc.z);
					params.world = bc.world;
					params.silent = true;
					params.needDate = true;
					params.needType = true;
					params.needData = true;
					// order descending and limit 1, we just want the most recent blockChange
					params.limit = 1;
					params.order = QueryParams.Order.DESC;
					try {
						for (de.diddiz.LogBlock.BlockChange lbChange : logBlock.getBlockChanges(params))
							lbOwner = lbChange.playerName;
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				log.logMessage(bc.time, "block broken at "
						+bc.locationString()
						+", blockType="+bc.type
						+", lbOwner="+lbOwner
						+", blockData="+bc.data
					);
			}
			else if( bc.eventType == Type.BLOCK_PLACE ) {
				log.logMessage(bc.time, "block placed at "
						+bc.locationString()
						+", blockType="+bc.type
						+", blockData="+bc.data
					);
			}
			else {
				logger.warning(logPrefix+"ERROR: unknown eventType in BlockLogger: "+bc.eventType);
			}
		}
	}
}
