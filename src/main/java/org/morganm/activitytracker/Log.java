/**
 * 
 */
package org.morganm.activitytracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.morganm.activitytracker.util.PermissionSystem;

/** Class that actually handles the logging activity.
 * 
 * @author morganm
 *
 */
public class Log {
	private static final long FLUSH_FREQUENCY_MILLIS = 5000;
	
	private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
	private static final Logger log = ActivityTracker.log;
	private static final String logPrefix = ActivityTracker.logPrefix;
	
	private final ActivityTracker plugin;
	private final String playerName;
	private String logDir;
	private boolean logDirPerGroup = false; 
	private File file;
	private Writer writer;
	
	private long lastFlush;
	
	public Log(ActivityTracker plugin, String playerName) {
		this.plugin = plugin;
		this.playerName = playerName;
		loadConfig();
	}
	
	public void loadConfig() {
		this.logDir = plugin.getConfig().getString("logDir");
		this.logDirPerGroup = plugin.getConfig().getBoolean("logDirPerGroup");
	}
	
	public void init() throws IOException {
		// try to close the writer if it already exists, silently catch any error
		if( writer != null ) {
			try {
				writer.close();
			}catch(Exception e) {}
			writer = null;
		}
		
		File fLogDir = null;
		if( logDirPerGroup ) {
			String group = PermissionSystem.getInstance().getPlayerGroup(null, playerName);
			if( group != null ) {
				fLogDir = new File(logDir + "/" + group);
				file = new File(logDir+"/"+group+"/"+playerName+".log");
			}
		}
		
		if( file == null ) 
			file = new File(logDir+"/"+playerName+".log");
		if( fLogDir == null )
			fLogDir = new File(logDir);
		
		if( !fLogDir.exists() )
			fLogDir.mkdirs();
		
		writer = new BufferedWriter(new FileWriter(file, true), 16384);
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
			
			if( (System.currentTimeMillis() - lastFlush) > FLUSH_FREQUENCY_MILLIS )
				writer.flush();
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
