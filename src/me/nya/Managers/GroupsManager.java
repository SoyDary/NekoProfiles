package me.nya.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import me.nya.NekoProfiles;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;

public class GroupsManager {
	NekoProfiles plugin;
	
	public GroupsManager(NekoProfiles plugin) {
		this.plugin = plugin;
		
	}
	
	public String getSelectedGroup(UUID uuid) {	
		String group = plugin.getDataManager().accounts.getString(uuid+".Groups.selected");		
		return group == null ? getPrimaryGroup(uuid) : group;
	}
	
	public void setSelectedGroup(UUID uuid, String group) {
		plugin.getDataManager().accounts.set(uuid+".Groups.selected", group);
		plugin.getDataManager().saveAccounts();
	}
	
	public User getLPUser(UUID uuid) {
	    UserManager userManager = plugin.getLuckPermsAPI().getUserManager();
	    CompletableFuture<User> userFuture = userManager.loadUser(uuid);
	    return userFuture.join();
	}
	
	public String getPrimaryGroup(UUID uuid) {
		User user = getLPUser(uuid);
	  	List<String> p_groups = new ArrayList<String>();    
		for(Group group :plugin.getLuckPermsAPI().getGroupManager().getLoadedGroups()) {
			InheritanceNode inheritanceNode = InheritanceNode.builder(group).build();
			if(user.data().contains(inheritanceNode, NodeEqualityPredicate.EXACT).asBoolean()) {
				p_groups.add(group.getName());	
			}									
		}
		for(int i = plugin.getDataManager().group_sorter.size(); i >= 0; i--) {
			String gn = plugin.getDataManager().group_sorter.get(i);
			if(p_groups.contains(gn)) {
				return gn;
			}
		}
		return "default";	
	}
	public List<String> getGroups(UUID uuid) {
		User user = getLPUser(uuid);
	  	List<String> p_groups = new ArrayList<String>();    
		for(Group group :plugin.getLuckPermsAPI().getGroupManager().getLoadedGroups()) {
			InheritanceNode inheritanceNode = InheritanceNode.builder(group).build();
			if(user.data().contains(inheritanceNode, NodeEqualityPredicate.EXACT).asBoolean()) {
				p_groups.add(group.getName());	
			}									
		}
		if(!p_groups.contains("default")) p_groups.add("default");
		return p_groups;	
	}
}
