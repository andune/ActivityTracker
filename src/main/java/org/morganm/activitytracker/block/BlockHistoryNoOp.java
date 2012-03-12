/**
 * 
 */
package org.morganm.activitytracker.block;

import org.bukkit.Location;

/**
 * @author morganm
 *
 */
public class BlockHistoryNoOp implements BlockHistoryManager {

	@Override
	public BlockHistory getBlockHistory(Location l) {
		return null;
	}

}
