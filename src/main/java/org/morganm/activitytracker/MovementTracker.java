/**
 * 
 */
package org.morganm.activitytracker;

/** Rather than hooking the expensive PLAYER_MOVE and forcing the Bukkit overhead associated with
 * producing and processing those events on every PLAYER_MOVE, we instead schedule a process to
 * check player locations of currently tracked players, which is a much smaller list than every
 * player and therefore a lot less processing. Further, because we aren't changing anything, we
 * can do this asynchronously so it can run on a separate thread/processor than the main thread.
 * 
 * @author morganm
 *
 */
public class MovementTracker implements Runnable {
	private ActivityTracker plugin;
	
	public MovementTracker(ActivityTracker plugin) {
		this.plugin = plugin;
	}
	
	public void run() {
		// TODO: loop over currently tracked entities and record location changes
	}
}