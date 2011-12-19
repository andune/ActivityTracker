/**
 * 
 */
package org.morganm.activitytracker.util;

import java.io.File;

/** Utility methods related to players.
 * 
 * @author morganm
 *
 */
public class PlayerUtil {
	private static PlayerUtil instance;
	
	private PlayerUtil() {}
	
	/** Singleton pattern.
	 * 
	 * @return
	 */
	public static PlayerUtil getInstance() {
		if( instance == null )
			instance = new PlayerUtil();
		return instance;
	}
	
    public boolean isNewPlayer(String playerName) {
    	boolean newPlayerFlag = true;
    	
    	String playerDat = playerName + ".dat";
    	
    	File file = new File("world/players/"+playerDat);
    	if( file.exists() )
    		newPlayerFlag = false;
    	
    	Debug.getInstance().debug("isNewPlayer() playerName=",playerName,", result=",newPlayerFlag);
    	
    	// if we didn't find any record of this player on any world, they must be new
    	return newPlayerFlag;
    }

}
