/**
 * 
 */
package org.morganm.activitytracker.block;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.event.Event.Type;
import org.morganm.activitytracker.ActivityTracker;
import org.morganm.activitytracker.Log;
import org.morganm.activitytracker.LogManager;
import org.morganm.activitytracker.util.Debug;

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
	private BlockHistoryManager blockHistoryManager;
	private boolean isCanceled = false;
	
	public BlockLogger(final ActivityTracker plugin) {
		this.plugin = plugin;
		this.tracker = this.plugin.getBlockTracker();
		this.logManager = this.plugin.getLogManager();
		this.blockHistoryManager = BlockHistoryFactory.getBlockHistoryManager(plugin);
		this.debug = Debug.getInstance();
	}
	
	public void cancel() {
		isCanceled = true;
		this.blockHistoryManager = null;
	}

	public void run() {
		if( isCanceled )
			return;
		
		BlockChange bc = null;
		
		// run until the queue is empty
		while( (bc = tracker.getStartObject()) != null ) {
			debug.debug("BlockLogger.run(): queue has an object pending, processing");
			Log log = logManager.getLog(bc.playerName);
			Location l = bc.getLocation();
			
			if( bc.eventType == Type.BLOCK_BREAK ) {
				String blockOwner = "(none)";
				
				if( blockHistoryManager != null ) {
					BlockHistory bh = blockHistoryManager.getBlockHistory(l);
					
					// we only count owner if the block destroyed was the same type placed,
					// this avoids logging when people cut down trees that were previously
					// saplings planted by players, etc
					if( bh != null && bh.getTypeId() == bc.type.getId() )
						blockOwner = bh.getOwner();
				}
				
				String postMsg = "";
				if( "(none)".equals(blockOwner) )
					debug.debug("no block owner found");
				else if( !bc.playerName.equals(blockOwner) )
					postMsg = " ** NOT BLOCK OWNER **";
				
				log.logMessage(bc.time, "block broken at "
						+bc.locationString()
						+", blockType="+bc.type
						+", lbOwner="+blockOwner
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
