package me.nya.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.nya.NekoProfiles;
import me.nya.Objects.NekoGroup;
import me.nya.Objects.RanksSelectorGUI;

public class NekoGroups implements CommandExecutor , TabCompleter{
	private NekoProfiles plugin = NekoProfiles.getInstance();
	

	@Override	
	public boolean onCommand(CommandSender s, Command cmd, String l, String[] a) {	
		String label = cmd.getLabel();
		if(label.equalsIgnoreCase("nekogroups")) {
			Player p = s instanceof Player ? (Player) s : null;
			if(a.length == 0) {
				RanksSelectorGUI gui = new RanksSelectorGUI(p);
				p.openInventory(gui.getInventory());
			} else {
				
				if(a[0].equalsIgnoreCase("list")) {
					for(int i = plugin.getDataManager().neko_groups.size()-1 ; i >= 0; i--) {
						NekoGroup group = plugin.getDataManager().neko_groups.get(plugin.getDataManager().group_sorter.get(i));
						p.sendMessage("§8[§6"+group.height+"§8]  §f"+group.name+"   "+plugin.getUtils().color(group.prefix)+" "+group.sub_prefixes.size());		
						if(group.sub_prefixes.size() > 0) {
							for(String str : group.sub_prefixes.keySet()) {
								p.sendMessage("  >  "+str+" "+plugin.getUtils().color(group.sub_prefixes.get(str)));
							}
						}	
						
					}
				}
				if(a[0].equalsIgnoreCase("selectgroup")) {
					if(a.length == 1) {
						for(String str : plugin.getGroupsManager().getGroups(p.getUniqueId())) {
							p.sendMessage("§3"+str);
						}
						return true;
					}
					NekoGroup group = null;
					boolean reset = false;
					if(a[1].toLowerCase().equals("--reset")) {
						reset = true;
					} else {
						group = plugin.getDataManager().neko_groups.get(a[1].toLowerCase());
						if(group == null) {
							p.sendMessage("§cNo existe este grupo!");
							return true;
						}
					}

					if(a.length == 2) {
						if(reset) {
							plugin.getGroupsManager().setSelectedGroup(p.getUniqueId(), null);
							p.sendMessage(plugin.getUtils().color("&#ff6699» &#ffe6ff¡Apariencia reestablecida!"));
							return true;
						}
						if(!p.hasPermission("nekoprofiles.groups.admin")) {
							if(!plugin.getGroupsManager().getGroups(p.getUniqueId()).contains(group.name)) {
								p.sendMessage(plugin.getUtils().color("&#ff3300» &c¡No posees este rango para poder seleccionarlo!"));
								return true;
							}
						}
						String prefix = plugin.getUtils().color(group.prefix);
						String selected_g = plugin.getGroupsManager().getSelectedGroup(p.getUniqueId());
						if(selected_g.equals(group.name)) {
							p.sendMessage(plugin.getUtils().color("&#ffd11a» &#ffd480¡Ya tienes esta apariencia de rango seleccionada!"));
						} else {
							plugin.getGroupsManager().setSelectedGroup(p.getUniqueId(), group.name);
							p.sendMessage(plugin.getUtils().color("&9» &#80ccffApariencia de rango actualizada a "+prefix));
						}
					} 					

					if(a.length == 3) {
						if(!p.hasPermission("nekoprofiles.admin")) {
							p.sendMessage("§cNo tienes permiso de cambiar el grupo de otros jugadores");
							return true;
						}
						Player p2 = Bukkit.getPlayer(a[2]);
						if(p2 == null) {
							p.sendMessage("§cNo se encontró al jugador §e"+a[2]);		
						} else {
							if(reset) {
								plugin.getGroupsManager().setSelectedGroup(p2.getUniqueId(), null);
								p.sendMessage("Reestableciste el grupo visible de §e"+p2.getName());		
								p2.sendMessage("Grupo visible reestablecido!");
								return true;
							}
							if(!p.hasPermission("nekoprofiles.groups.admin")) {
								if(!plugin.getGroupsManager().getGroups(p.getUniqueId()).contains(group.name)) {
									p.sendMessage(plugin.getUtils().color("&#ff3300» &c¡No posees este rango para poder seleccionarlo!"));
									return true;
								}
							}
							String prefix = plugin.getUtils().color(group.prefix);
							String selected_g = plugin.getGroupsManager().getSelectedGroup(p2.getUniqueId());
							if(selected_g.equals(group.name)) {
								p.sendMessage("§e"+p2.getName()+" §c ya tiene seleccionado este grupo");
							} else {
								plugin.getGroupsManager().setSelectedGroup(p2.getUniqueId(), group.name);
								p.sendMessage("Cambiaste el rango visible de §e"+p2.getName()+" §ra "+prefix);							
								p2.sendMessage("Tu rango visible fue cambiado a "+prefix);
							}

						}
						
					} 

				}
			}
		}
		return true;
	}
	

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String l, String[] a) {	
		String label = cmd.getLabel();
	    if (label.equalsIgnoreCase("nekogroups")) {
	    	if(!s.hasPermission("nekoprofiles.groups.admin")) return null;
	        if (a.length == 1) {
		          List<String> commandsList = new ArrayList<>();
		          List<String> preCommands = new ArrayList<>();
		          commandsList.add("list");
		          commandsList.add("selectgroup");
		          for (String text : commandsList) {
		            if (text.toLowerCase().startsWith(a[0].toLowerCase()))
		              preCommands.add(text); 
		          } 
		          return preCommands;
	        } else {
	        	if (a.length == 2) {
		        	if(a[0].equalsIgnoreCase("selectgroup")) {
				          List<String> commandsList = new ArrayList<>();
				          List<String> preCommands = new ArrayList<>();
				          for(String str : plugin.getDataManager().neko_groups.keySet()) {
				        	  commandsList.add(str);
				        	  commandsList.add("--reset");
				          }
				          for (String text : commandsList) {
					            if (text.toLowerCase().startsWith(a[1].toLowerCase()))
					              preCommands.add(text); 
				          }
				          return preCommands;    
		        	}
	        	}

	        }
	    }
		return null;
	}
}