/**
 * 
 */
package org.morganm.activitytracker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Logger;

/** Class that actually handles the logging activity.
 * 
 * @author morganm
 *
 */
public class Log {
	// TODO: externalize to config
	private static final String LOG_DIR = "plugins/ActivityTracker/logs";
	
	private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
	private static final Logger log = ActivityTracker.log;
	private static final String logPrefix = ActivityTracker.logPrefix;
	
//	private ActivityTracker plugin;
	private String playerName;
	private File file;
	private FileWriter writer;
	
	public Log(ActivityTracker plugin, String playerName) {
//		this.plugin = plugin;
		this.playerName = playerName;
	}
	
	public void init() throws IOException {
		// try to close the writer if it already exists, silently catch any error
		if( writer != null ) {
			try {
				writer.close();
			}catch(Exception e) {}
			writer = null;
		}
		
		file = new File(LOG_DIR+"/"+playerName);
		writer = new FileWriter(file);
	}
	
	public void close() {
		try {
			if( writer != null )
				writer.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logMessage(long msgTime, String message) {
		String msg = "[" + dateFormat.format(new Date(msgTime)) + "] " + message + "\n";
		try {
			if( writer == null )
				init();
			writer.append(msg);
		}
		catch(IOException e) {
			log.warning(logPrefix+"Error ("+e.getMessage()+") writing log message: "+msg);
			e.printStackTrace();
			
			// also try to run init() again, in case the logfile was closed
			try {
				init();
			} catch(Exception e1) {}
		}
	}
	public void logMessage(String message) {
		logMessage(System.currentTimeMillis(), message);
	}
}
