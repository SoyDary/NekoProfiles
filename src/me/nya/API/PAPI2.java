package me.nya.API;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.nya.NekoProfiles;
import me.nya.Objects.NekoGroup;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;

public class PAPI2 extends PlaceholderExpansion {
	DiscordSRV discord = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");
	JDA jda = discord.getJda();
	
    private NekoProfiles plugin;
  

    public PAPI2(NekoProfiles plugin) {
    	this.plugin = plugin;
    }
    
    
    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }


    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().get(0);
    }
 

    @Override
    public String getIdentifier(){
        return "nekoprofiles";
    }


    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }
  
    
    @Override
    public String onPlaceholderRequest(Player p, String id) {
    	if(p != null) {
    		NekoGroup group = getNekoGroup(p);
    		if(id.equalsIgnoreCase("group_prefix")) {
    			return group.prefix;
    		}
    		if(id.equalsIgnoreCase("group_prefix_colored")) {
    			return plugin.getUtils().color(group.prefix);
    		}
    		if(id.equalsIgnoreCase("group_alias")) {
    			return group.alias;
    		}
    		if(id.equalsIgnoreCase("group_alias_colored")) {
    			return plugin.getUtils().color(group.alias);
    		}
    		if(id.equalsIgnoreCase("group_height")) {
    			return String.valueOf(group.height);
    		}
    		
    	} else {
    		return id;
    	}
		return null;
    	
       
    }
    
    public NekoGroup getNekoGroup(Player p) {
    	return plugin.getDataManager().neko_groups.get(plugin.getGroupsManager().getSelectedGroup(p.getUniqueId()));
    }  
      
}
