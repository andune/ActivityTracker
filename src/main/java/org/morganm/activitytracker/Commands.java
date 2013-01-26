/**
 * 
 */
package org.morganm.activitytracker;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import com.andune.minecraft.commonlib.PermissionSystem;

/** Class for processing commands.
 * 
 * @author morganm
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
