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
import org.morganm.activitytracker.util.Debug;

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
	
	private final ActivityTracker plugin;
	private final BlockTracker tracker;
	private final LogManager logManager;
	private final Debug debug;
	private LogBlock logBlock;
	private boolean isCanceled = false;
	
	public BlockLogger(ActivityTracker plugin) {
		this.plugin = plugin;
		this.tracker = this.plugin.getBlockTracker();
		this.logManager = this.plugin.getLogManager();
		
		Plugin p = this.plugin.getServer().getPluginManager().getPlugin("LogBlock");
		if( p instanceof LogBlock )
			this.logBlock = (LogBlock) p;
		else
			this.logBlock = null;
		
		this.debug = Debug.getInstance();
	}
	
	public void cancel() {
		isCanceled = true;
		this.logBlock = null;
	}

	public void run() {
		if( isCanceled )
			return;
		
		BlockChange bc = null;
		
		// run until the queue is empty
		while( (bc = tracker.getStartObject()) != null ) {
			debug.debug("BlockLogger.run(): queue has an object pending, processing");
			Log log = logManager.getLog(bc.playerName);
			
			if( bc.eventType == Type.BLOCK_BREAK ) {
				String lbOwner = "(none)";
				
				// if it's a broken block and we have logBlock, lookup the owner
				if( logBlock != null ) {
					debug.debug("running logBlock query");
					QueryParams params = new QueryParams(logBlock);
					params.bct = BlockChangeType.CREATED;
//					params.since = 43200;		// 30 days
					params.since = 107373;		// roughly 3 months
					params.loc = new Location(bc.world, bc.x, bc.y, bc.z);
					params.world = bc.world;
					params.silent = true;
//					params.needDate = true;
					params.needType = true;
					params.needPlayer = true;
					params.radius = 0;
					// order descending and limit 1, we just want the most recent blockChange
					params.limit = 1;
					params.order = QueryParams.Order.DESC;
					try {
						if( debug.isDevDebug() ) {
							debug.devDebug("logBlock query = ",params.getQuery());
						}
						for (de.diddiz.LogBlock.BlockChange lbChange : logBlock.getBlockChanges(params)) {
							// we only count owner if the block destroyed was the same type placed,
							// this avoids logging when people cut down trees that were previously
							// saplings planted by players, etc
							if( lbChange.type == bc.type.getId() )
								lbOwner = lbChange.playerName;
							debug.debug("got logBlock result, lbOwner=",lbOwner);
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				String postMsg = "";
				if( "(none)".equals(lbOwner) )
					debug.debug("no logBlock owner found");
				else if( !bc.playerName.equals(lbOwner) )
					postMsg = " ** NOT BLOCK OWNER **";
				
				log.logMessage(bc.time, "block broken at "
						+bc.locationString()
						+", blockType="+bc.type
						+", lbOwner="+lbOwner
						+", blockData="+bc.data
						+postMsg
					);
			}
			else if( bc.eventType == Type.BLOCK_PLACE ) {
				log.logMessage(bc.time, "block placed at "
						+bc.locationString()
						+", blockType="+bc.type
						+", blockData="+bc.data
					);
			}
			else if( bc.eventType == Type.SIGN_CHANGE ) {
				// record sign data, if any
				StringBuilder signData = new StringBuilder();
				if( bc.signData != null ) {
					for(int i=0; i < bc.signData.length; i++) {
						if( signData.length() > 0 )
							signData.append("|");
						signData.append(bc.signData[i]);
					}
					signData.insert(0, ", Sign data: ");
				}
				
				log.logMessage(bc.time, "sign text changed at "
						+bc.locationString()
						+", blockType="+bc.type
						+", blockData="+bc.data
						+signData
					);
			}
			else {
				logger.warning(logPrefix+"ERROR: unknown eventType in BlockLogger: "+bc.eventType);
			}
		}
	}
}
