/**
 * 
 */
package org.morganm.activitytracker.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.morganm.activitytracker.util.General;

import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;

/** Code originally copied from @Diddiz's LogBlock plugin.
 * 
 * @author morganm
 *
 */
public class MySpoutInventoryListener extends InventoryListener {
	private final Map<Player, ItemStack[]> containers = new HashMap<Player, ItemStack[]>();
	private final General util;
	
	public MySpoutInventoryListener() {
		util = General.getInstance();
	}
	
	class LBSpoutChestAccessListener extends InventoryListener
	{
		private final Consumer consumer;
		private final Map<Player, ItemStack[]> containers = new HashMap<Player, ItemStack[]>();

		LBSpoutChestAccessListener(LogBlock logblock) {
			consumer = logblock.getConsumer();
		}

		@Override
		public void onInventoryClose(InventoryCloseEvent event) {
			if (!event.isCancelled() && event.getLocation() != null) {
				final Player player = event.getPlayer();
				final ItemStack[] before = containers.get(player);
				if (before != null) {
					final ItemStack[] after = util.compressInventory(event.getInventory().getContents());
					final ItemStack[] diff = util.compareInventories(before, after);
					final Location loc = event.getLocation();
					for (final ItemStack item : diff) {
//						consumer.queueChestAccess(player.getName(), loc, loc.getWorld().getBlockTypeIdAt(loc), (short)item.getTypeId(), (short)item.getAmount(), util.rawData(item));
					}
					containers.remove(player);
				}
			}
		}

		@Override
		public void onInventoryOpen(InventoryOpenEvent event) {
			if (!event.isCancelled() && event.getLocation() != null && event.getLocation().getBlock().getTypeId() != 58)
				containers.put(event.getPlayer(), util.compressInventory(event.getInventory().getContents()));
		}
	}
}
