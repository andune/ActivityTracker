/**
 * 
 */
package org.morganm.activitytracker.util;

import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.wepif.PermissionsResolverManager;

/** Permission abstraction class, use Vault, WEPIF, Perm2 or superperms, depending on what's available.
 * 
 * @author morganm
 *
 */
public class PermissionSystem {
	private static PermissionSystem instance;
	
	private final JavaPlugin plugin;
	private final Logger log;
	private final String logPrefix;
	
    private net.milkbowl.vault.permission.Permission vaultPermission = null;
    private PermissionsResolverManager wepifPerms = null;
    private PermissionHandler perm2Handler;
	
	public PermissionSystem(JavaPlugin plugin, Logger log, String logPrefix) {
		this.plugin = plugin;
		if( log != null )
			this.log = log;
		else
			this.log = Logger.getLogger(PermissionSystem.class.toString());
		
		if( logPrefix != null )
			this.logPrefix = logPrefix;
		else
			this.logPrefix = "["+plugin.getDescription().getName()+"] ";
		
		instance = this;
	}
	
	/** **WARNING** Not your typical singleton pattern, this CAN BE NULL. An instance
	 * must be created by the plugin before this will return a value.
	 * 
	 * @return
	 */
	public static PermissionSystem getInstance() {
		return instance;
	}
	
	public void setupPermissions() {
		if( !setupVaultPermissions() )
			if( !setupWEPIFPermissions() ) {
				if( !setupPerm2() ) {
					log.warning(logPrefix+" No Vault, WEPIF or Perm2 perms found, permissions functioning in degraded mode (superperms does NOT support prelogin or offline permissions).");
				}
			}
	}
	
    /** Check to see if player has a given permission.
     * 
     * @param p The player
     * @param permission the permission to be checked
     * @return true if the player has the permission, false if not
     */
    public boolean has(CommandSender sender, String permission) {
    	Player p = null;
    	// console always has access
    	if( sender instanceof ConsoleCommandSender )
    		return true;
    	if( sender instanceof Player )
    		p = (Player) sender;
    	
    	if( p == null )
    		return false;
    	
    	if( vaultPermission != null )
    		return vaultPermission.has(p, permission);
    	else if( wepifPerms != null )
    		return wepifPerms.hasPermission(p.getName(), permission);
    	else if( perm2Handler != null ) 
    		return perm2Handler.has(p, permission);
    	else
    		return p.hasPermission(permission);		// fall back to superperms
    }
    
    public boolean has(String world, String player, String permission) {
    	if( vaultPermission != null )
    		return vaultPermission.has(world, player, permission);
    	else if( wepifPerms != null )
    		return wepifPerms.hasPermission(player, permission);
    	else if( perm2Handler != null )
    		return perm2Handler.has(world, player, permission);
    	else
    		return false;	// no options with superperms
    }
    public boolean has(String player, String permission) {
    	return has("world", player, permission);
    }
    
    private boolean setupPerm2() {
        Plugin permissionsPlugin = plugin.getServer().getPluginManager().getPlugin("Permissions");
        if( permissionsPlugin != null ) {
        	perm2Handler = ((Permissions) permissionsPlugin).getHandler();
        	log.info(logPrefix+"Perm2 permissions found and enabled");
        }
        	
        return (perm2Handler != null);
    }
    
    private boolean setupVaultPermissions()
    {
    	Plugin vault = plugin.getServer().getPluginManager().getPlugin("Vault");
    	if( vault != null ) {
	        RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
	        if (permissionProvider != null) {
	        	log.info(logPrefix+"Vault permissions found and enabled");
	            vaultPermission = permissionProvider.getProvider();
	        }
    	}
//    	else
//        	Debug.getInstance().debug("Vault permissions not found");
    	
        return (vaultPermission != null);
    }
    
    private boolean setupWEPIFPermissions() {
    	try {
	    	Plugin worldEdit = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
	    	if( worldEdit != null ) {
	    		wepifPerms = PermissionsResolverManager.getInstance();
//	    		wepifPerms.initialize(plugin);
//		    	wepifPerms = new PermissionsResolverManager(this, "LoginLimiter", log);
//		    	(new PermissionsResolverServerListener(wepifPerms, this)).register(this);
	    		log.info(logPrefix+"WEPIF permissions enabled");
	    	}
    	}
    	catch(Exception e) {
    		log.info(logPrefix + " Unexpected error trying to setup WEPIF permissions hooks (this message can be ignored): "+e.getMessage());
    	}
    	
    	return wepifPerms != null;
    }
}
