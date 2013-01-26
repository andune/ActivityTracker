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
package com.andune.minecraft.activitytracker;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import com.andune.minecraft.commonlib.PermissionSystem;

/** Class for processing commands.
 * 
 * @author andune
 *
 */
public class Commands {
	private final ActivityTracker plugin;
	private final PermissionSystem perms;
	
	public Commands(ActivityTracker plugin) {
		this.plugin = plugin;
		this.perms = plugin.getPerm();
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String cmdName = command.getName();
		
		if( !perms.has(sender, command.getPermission()) ) {
			sender.sendMessage(ChatColor.DARK_RED + "No permission");
			return true;
		}
		
		if( "at".equals(cmdName) ) {
			if( args.length > 0 ) {
				if( args[0].equals("reload") ) { 
					plugin.loadConfig();
					sender.sendMessage(ChatColor.YELLOW+plugin.getDescription().getName()+" config file reloaded");
				}
				else
					sender.sendMessage(command.getUsage());
			}
			else
				sender.sendMessage(command.getUsage());
			
			return true;
		}
		else if( "track".equals(cmdName) ) {
			if( args.length > 0 ) {
				Player p = plugin.getServer().getPlayer(args[0]);
				if( p != null ) {
					if( !plugin.getTrackerManager().isTracked(p) ) {
						plugin.getTrackerManager().trackPlayer(p);
						sender.sendMessage(ChatColor.YELLOW+"Player "+p+" is now being tracked.");
					}
					else
						sender.sendMessage(ChatColor.YELLOW+"Player "+p+" is already being tracked.");
				}
				else
					sender.sendMessage(ChatColor.YELLOW+"No player named \""+args[0]+"\" is online");
			}
			else
				sender.sendMessage(command.getUsage());
			
			return true;
		}
		else if( "untrack".equals(cmdName) ) {
			if( args.length > 0 ) {
				Player p = plugin.getServer().getPlayer(args[0]);
				if( p != null ) {
					if( plugin.getTrackerManager().isTracked(p) ) {
						plugin.getTrackerManager().unTrackPlayer(p);
						sender.sendMessage(ChatColor.YELLOW+"Player "+p+" is no longer being tracked.");
					}
					else
						sender.sendMessage(ChatColor.YELLOW+"Player "+p+" is not being tracked.");
				}
				else
					sender.sendMessage(ChatColor.YELLOW+"No player named \""+args[0]+"\" is online");
			}
			else
				sender.sendMessage(command.getUsage());
			
			return true;
		}
		else if( "tracklist".equals(cmdName) ) {
			Set<HumanEntity> trackedPlayers = plugin.getTrackerManager().getTrackedPlayers();
			StringBuilder sb = new StringBuilder();
			for(HumanEntity p : trackedPlayers) {
				if( sb.length() > 0 )
					sb.append(", ");
				sb.append(p.getName());
			}
			if( sb.length() == 0 )
				sb.append("(none");
			
			sender.sendMessage(ChatColor.YELLOW+"Currently tracked players: "+sb.toString());
		}

		return false;
	}
}
