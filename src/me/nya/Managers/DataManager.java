package me.nya.Managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import me.nya.NekoProfiles;
import me.nya.Objects.NekoGroup;
import me.nya.Objects.NekoProfile;
import net.md_5.bungee.api.chat.TextComponent;


public class DataManager {
	
	public NekoProfiles plugin = NekoProfiles.getPlugin(NekoProfiles.class);
	public File dataFile;
	public FileConfiguration data;
	public File UsersFile;
	public FileConfiguration users;
	public File AccountsFile;
	public FileConfiguration accounts;
	public HashMap<UUID, List<String>> nameHistory = new HashMap<UUID, List<String>>();
	public HashMap<UUID, TextComponent> name_history_component = new HashMap<UUID, TextComponent>();
	public HashMap<UUID, Boolean> premiums = new HashMap<UUID, Boolean>();
	private DiscordSRV discord = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");
	public HashMap<String, String> users_map = new HashMap<String, String>();
	public List<String> valid_accounts = new ArrayList<String>();
	public HashMap<Integer, String> group_sorter = new HashMap<Integer, String>();
	public HashMap<String, NekoGroup> neko_groups = new HashMap<String, NekoGroup>();
	
	public DataManager(NekoProfiles plugin) {
		this.dataFile = new File(plugin.getDataFolder(), "data.yml");
		this.data = (FileConfiguration)YamlConfiguration.loadConfiguration(this.dataFile);
		this.AccountsFile = new File(plugin.getDataFolder(), "accounts.yml");
		this.accounts = (FileConfiguration)YamlConfiguration.loadConfiguration(this.AccountsFile);
		this.UsersFile = new File(plugin.getDataFolder(), "users.yml");
		this.users = (FileConfiguration)YamlConfiguration.loadConfiguration(this.UsersFile);

	}
	
	public void registerUsers() {
		Set<String> acts =accounts.getConfigurationSection("").getKeys(false);	
		for(String key : users.getConfigurationSection("").getKeys(false)) {
			String uuid = users.getString(key);
			if(acts.contains(uuid)) this.valid_accounts.add(uuid);
			users_map.put(key, uuid);
		}
	}
	public void loadGroups() {
		List<String> group_order = plugin.getConfig().getStringList("Groups.group_sorting");		
		int y = group_order.size()-1;
		for(int i = 0; i < group_order.size(); i++) {	
			String name = group_order.get(i);
			String prefix = plugin.getConfig().getString("Groups.group_prefixes."+name);
			String alias = plugin.getConfig().getString("Groups.group_aliases."+name);
			HashMap<String, String> sub_prefixes = new HashMap<String, String>();
			if(plugin.getConfig().isConfigurationSection("Groups.sub_prefixes."+name)) {
				for(String key : plugin.getConfig().getConfigurationSection("Groups.sub_prefixes."+name).getKeys(false)) {
					String s_prefix = plugin.getConfig().getString("Groups.sub_prefixes."+name+"."+key);
					if(s_prefix != null) sub_prefixes.put(key, s_prefix);
					
				}
			}
			NekoGroup group = new NekoGroup(name, prefix == null ? name : prefix, alias == null ? prefix : alias, y, sub_prefixes);
			this.group_sorter.put(y, name);
			this.neko_groups.put(name, group);
			y--;
		}
		
	}
	
	public void addTempRole(Member member, Role role, Long time) {
		plugin.getDiscord().getMainGuild().addRoleToMember(member, role).queue((e) -> {
			this.data.set(member.getId()+"."+role.getId()+".date", new Date().getTime());
			this.data.set(member.getId()+"."+role.getId()+".expire", time);
			this.saveData();
		});
	}
	
	public List<String> getUsers() {
		List<String> users = new ArrayList<String>();
		for(String str: plugin.getDataManager().users.getConfigurationSection("").getKeys(true)) { 
			users.add(str);
		}
		return users;
	}
	public String getUUIDFromName(String username) {
		return users.getString(username);
	}
	public boolean isExactUser(String user) {
		boolean bl = false;
		if(user.length() < 30) {
			for(String str : getUsers()) {
				if(str.equals(user)) {
					bl = true;
					break;
				}
			}	
		} else {
			for(String str : getUsers()) {
				if(users.getString(str).equals(user)) {
					bl = true;
					break;
				}
					
			}
		}
		return bl;
	
	}
	public boolean isUser(String user) {
		boolean bl = false;
		if(user.length() < 30) {
			for(String str : getUsers()) {
				if(str.equalsIgnoreCase(user)) {
					bl = true;
					break;
				}
			}	
		} else {
			for(String str : getUsers()) {
				if(users.getString(str).equalsIgnoreCase(user)) {
					bl = true;
					break;
				}
					
			}
		}
		return bl;
	
	}
	public String getUserUUID(String user) {
		String uuid = "";
		if(users.getString("user") != null) {
			return users.getString(user);
		} else {
			for(String str : users.getConfigurationSection("").getKeys(false)) {
				if(str.equalsIgnoreCase(user)) {
					uuid = users.getString(str);
			
				} 
				
			}
			return uuid;
		}
	}
	
	public String getUUIDFromDiscordID(String discordID) {
		Map<String, UUID> accounts = discord.getAccountLinkManager().getLinkedAccounts();
		if(accounts.containsKey(discordID)) {
			return accounts.get(discordID).toString();
			
		} else {
			return null;
		}
		
		
	}
	public void updateUserUUID(Player p) {
		NekoProfile profile = new NekoProfile(p.getUniqueId().toString());
		String oldName = profile.getUserName();
		if(oldName == null) oldName = p.getName();
		
		if(!p.getName().equals(oldName)) {
			users.set(oldName, null);
			users.set(p.getName(), p.getUniqueId().toString());
			saveUsers();	
			Bukkit.getConsoleSender().sendMessage("[NekoProfiles] Nombre de "+oldName+" actualizado a "+p.getName());
		} else {
			users.set(p.getName(), p.getUniqueId().toString());
			saveUsers();	
		}
	}
	public String getBirthDay(String uuid) {
		return accounts.getString(uuid+".birthday.date");
	}

	public void setBirthDay(String uuid, String date) {
		accounts.set(uuid+".birthday.date", date);
		saveAccounts();
	}

	public void saveUsers() {
		try {
			this.users.save(this.UsersFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void saveData() {
		try {
			this.data.save(this.dataFile);
			this.data = (FileConfiguration)YamlConfiguration.loadConfiguration(this.dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void saveAccounts() {
		try {
			this.accounts.save(this.AccountsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void onStart() {
		if(!this.AccountsFile.exists()) {
			saveAccounts();
		}
		registerUsers();
		loadGroups();
	}
	public void saveFile(File file, FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}


}