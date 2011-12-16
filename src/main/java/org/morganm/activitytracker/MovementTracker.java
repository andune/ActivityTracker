/**
 * 
 */
package org.morganm.activitytracker;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.morganm.activitytracker.util.Debug;
import org.morganm.activitytracker.util.General;

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
	private final ActivityTracker plugin;
	private final TrackerManager trackerMgr;
	private final LogManager logManager;
	private final HashMap<Player,Location> positions = new HashMap<Player,Location>(10);
	private final Debug debug;
	private final General util;
	
	private final DecimalFormat df = new DecimalFormat("#.#");
	
	public MovementTracker(ActivityTracker plugin) {
		this.plugin = plugin;
		this.trackerMgr = this.plugin.getTrackerManager();
		this.logManager = this.plugin.getLogManager();
		this.debug = Debug.getInstance();
		this.util = General.getInstance();
	}
	
	public void playerLogout(Player p) {
		positions.remove(p);
	}
	
	public void run() {
		Set<Player> players = trackerMgr.getTrackedPlayers();
		for(Player p : players) {
			Location curPos = p.getLocation();
			Location prevPos = positions.get(p);
			if( prevPos == null ) {
				positions.put(p, curPos);
				continue;
			}
			positions.put(p, curPos);
			
			debug.debug("MovementTracker.run(): processing movement for player ",p);
			
			String curWorld = curPos.getWorld().getName();
			String prevWorld = prevPos.getWorld().getName();
			Log log = logManager.getLog(p.getName());
			if( !curWorld.equals(prevWorld)
					|| curPos.getBlockX() != prevPos.getBlockX()
					|| curPos.getBlockY() != prevPos.getBlockY()
					|| curPos.getBlockZ() != prevPos.getBlockZ() ) {
				String distanceString = null;
				if( curWorld.equals(prevWorld) )
					distanceString = df.format(curPos.distance(prevPos));
				else
					distanceString = "(crossworld)";
				
				log.logMessage("player moved distance "
						+ distanceString
						+", curLoc="+util.shortLocationString(curPos)
						+", prevLoc="+util.shortLocationString(prevPos));
			}
		}
	}
}