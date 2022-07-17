package me.nya.Objects;

import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.nya.NekoProfiles;
import me.nya.Utils.ItemUtils;

public class NekoGroup {
	
	NekoProfiles plugin = NekoProfiles.getInstance();
	public String name = "";
	public String prefix = "";
	public String alias = "";
	public Integer height = 0;
	public ItemStack item = null;
	public ItemStack item_selected = null;
	public HashMap<String, String> sub_prefixes;
	
	
	public NekoGroup(String name, String prefix, String alias, int height, HashMap<String, String> sub_prefixes) {
		this.name = name;
		this.prefix = prefix;
		this.alias = alias;
		this.height = height;
		this.sub_prefixes = sub_prefixes;	
		try {loadItems();} catch (Exception e) {}
	}
	
	public String getSubPrefix(String str) {
		if(sub_prefixes.containsKey(str)) {
			return sub_prefixes.get(str);
		} 
		return this.prefix;
		
	}
	private void loadItems() {		
		primaryItem();
		secondaryItem();

		
	}
	private void primaryItem() {
		ItemStack item = null;
		String material = plugin.getConfig().getString("Groups.GUI."+name+".material");
		if(material == null) return;
		String i_name = plugin.getConfig().getString("Groups.GUI."+name+".name");
		i_name = setPlaceholders(i_name);
		List<String> lore = plugin.getConfig().getStringList("Groups.GUI."+name+".lore");
		item = ItemUtils.itemFromString(material);
		ItemUtils.setName(item, i_name);
		for(String l : lore) ItemUtils.addLoreLine(item, setPlaceholders(l));	
		ItemUtils.setTag(item, "NG_GROUP", name);
		this.item = item;
	}
	
	private void secondaryItem() {
		ItemStack item = null;
		String material = plugin.getConfig().getString("Groups.GUI."+name+"-selected.material");
		if(material == null) return;
		String i_name = plugin.getConfig().getString("Groups.GUI."+name+"-selected.name");
		i_name = setPlaceholders(i_name);
		List<String> lore = plugin.getConfig().getStringList("Groups.GUI."+name+"-selected.lore");
		item = ItemUtils.itemFromString(material);
		ItemUtils.setName(item, i_name);
		for(String l : lore) ItemUtils.addLoreLine(item, setPlaceholders(l));
		ItemUtils.setTag(item, "NG_GROUP", name);
		this.item_selected = item;
	}


	
	public String setPlaceholders(String str) {		
		return str
				.replaceAll("<group_prefix>", prefix)
		        .replaceAll("<group_name>", name)
		        .replaceAll("<group_alias>", alias);
	}
}

