/**
 * 
 */
package org.morganm.activitytracker;


/** Internally implements a circular buffer that reuses existing objects to avoid
 * any performance penalties related to object creation & GC.
 * 
 * TODO: known threading issues at the moment, consider how to add low-overhead synchronization
 * 
 * @author morganm
 *
 */
public class BlockTracker {
	private static final int BUFFER_MAX = 1000;
	
	private ActivityTracker plugin;
	private BlockChange[] buffer = new BlockChange[BUFFER_MAX];
	private int start = 0;
	private int end = 0;
	
	public BlockTracker(ActivityTracker plugin) {
		this.plugin = plugin;
	}

	/** Used to pop a BlockChange out of the buffer. This actually just moves circular buffer
	 * pointers, since we are not nulling out the underlying object.
	 * 
	 * @return
	 */
	public BlockChange getStartObject() {
		// empty buffer
		if( start == end )
			return null;
		
		synchronized (this) {
			if( ++start >= BUFFER_MAX )
				start = 0;
		}
		
		return buffer[start];
	}

	/** Used to get the next BlockChange event. The expectation is that the caller will modify
	 * this BlockChange event directly.
	 * 
	 * @return
	 */
	public BlockChange getEndObject() {
		if( ++end >= BUFFER_MAX )
			end = 0;
		
		// if the buffer is full, increment the start (essentially loosing that object)
		if( end == start ) {
			synchronized (this) {
				if( ++start >= BUFFER_MAX )
					start = 0;
			}
			// TODO: consider logging or throwing error here, this means we just wrapped a full buffer
		}

		if( buffer[end] == null )
			buffer[end] = new BlockChange();
		
		return buffer[end];
	}
}
