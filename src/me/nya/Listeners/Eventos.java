package me.nya.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.nya.NekoProfiles;
import me.nya.Objects.CakeDay;
import me.nya.Objects.NekoProfile;
import me.nya.Objects.RanksSelectorGUI;
import me.nya.Utils.ItemUtils;

public class Eventos implements Listener {	
	
	private NekoProfiles plugin = NekoProfiles.getPlugin(NekoProfiles.class); 

	@EventHandler	
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		join(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		leave(e.getPlayer());		
	}
	
	@EventHandler
	public void playerVehiclesClick(InventoryClickEvent e) {
		Inventory inv = e.getView().getTopInventory();
		if(e.getClickedInventory() == null) return;
		if(!(e.getInventory().getHolder() instanceof RanksSelectorGUI)) return;
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		if(e.getClickedInventory().equals(inv)) {	
			ItemStack item = e.getCurrentItem();
			if(item != null) { 	
				String tag = ItemUtils.getTag(item, "NG_GROUP");
				if(tag == null) return;
				Bukkit.dispatchCommand(p, "nekogroups selectgroup "+tag);
				p.closeInventory();
			}
		}
	}
	public void join(Player p) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				NekoProfile profile = new NekoProfile(p.getUniqueId().toString());	
				if(!plugin.getDataManager().premiums.containsKey(profile.getUniqueID())) plugin.getDataManager().premiums.put(profile.getUniqueID(), profile.isPremium());
				profile.updateLastPrefix();
				profile.updateSkinID();
				profile.updateLastSeen();
		        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		            public void run() {
		            	profile.updateGender();
		            }
		        }, 50L);
				profile.updatePlaceholders();
				plugin.getDataManager().updateUserUUID(p);	
				profile.updateUserName();

			}
		});
		CakeDay cumple = new CakeDay(p.getUniqueId().toString());
		if(cumple.isToday() && !cumple.hasBeenCelebratedInMinecraft()) cumple.announceInMinecraft();
	}
	public void leave(Player p) {
		NekoProfile profile = new NekoProfile(p.getUniqueId().toString());		
		profile.updateLastPrefix();
		profile.updateSkinID();
		profile.updateLastSeen();
		profile.updateGender();
		profile.updatePlaceholders();
		plugin.getDataManager().updateUserUUID(p);	
		profile.updateUserName();
	}

}
