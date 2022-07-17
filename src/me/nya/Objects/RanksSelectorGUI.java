package me.nya.Objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.nya.NekoProfiles;
import me.nya.Utils.ItemUtils;

public class RanksSelectorGUI implements InventoryHolder {

	NekoProfiles plugin = NekoProfiles.getInstance();
	List<String> groups = new ArrayList<String>();
	Inventory inv = Bukkit.createInventory(this, 45, "§6Selector de rangos");
	
	public RanksSelectorGUI(Player p) {
		ItemStack gray_glass = ItemUtils.emptyItem(Material.GRAY_STAINED_GLASS_PANE);
		for(int i = 0; i < inv.getSize(); i++) inv.setItem(i, gray_glass);
		ItemStack white_glass = ItemUtils.emptyItem(Material.WHITE_STAINED_GLASS_PANE);
		for(int i = 27; i < 36; i++) inv.setItem(i, white_glass);
		ItemStack reset = ItemUtils.namedItem(Material.BARRIER, "&c&o&nReestablecer apariencia");
		ItemUtils.addLoreLine(reset, "");
		ItemUtils.addLoreLine(reset, "&#ff6699» &#ffe6ffClic para reestablecer apariencia &#ff6699«");
		ItemUtils.setTag(reset, "NG_GROUP", "--reset");
		inv.setItem(44, reset);
		NekoGroup playergroup = plugin.getDataManager().neko_groups.get(plugin.getGroupsManager().getSelectedGroup(p.getUniqueId()));
		List<String> groups = plugin.getGroupsManager().getGroups(p.getUniqueId());
		int id = 0;
		for(int i = 0 ; i < plugin.getDataManager().neko_groups.size(); i++) {
			NekoGroup group = plugin.getDataManager().neko_groups.get(plugin.getDataManager().group_sorter.get(i));
			if(!groups.contains(group.name) && !p.hasPermission("nekoprofiles.groups.admin")) continue;
			if(group.item == null || group.item_selected == null) continue;
			if(group.name.equals(playergroup.name)){
				ItemStack selected = group.item_selected.clone();
				ItemUtils.setItemPlaceholders(p, selected);
				inv.setItem(id, selected);
			} else {
				ItemStack item = group.item.clone();
				ItemUtils.setItemPlaceholders(p, item);
				inv.setItem(id, item);
			}	
			id++;
		}
	}

	@Override
	public Inventory getInventory() {
		return this.inv;
	}

}
