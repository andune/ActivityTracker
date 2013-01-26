/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013 Andune (andune.alleria@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
/**
 * 
 */
package com.andune.minecraft.activitytracker.block;

import com.andune.minecraft.activitytracker.ActivityTracker;


/** Internally implements a circular buffer that reuses existing objects to avoid
 * any performance penalties related to object creation & GC.
 * 
 * @author andune
 *
 */
public class BlockTracker {
	private static final int BUFFER_MAX = 30000;
	
//	private ActivityTracker plugin;
	private BlockChange[] buffer = new BlockChange[BUFFER_MAX];
	private int start = 0;
	private int end = 0;
	
	public BlockTracker(ActivityTracker plugin) {
//		this.plugin = plugin;
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
