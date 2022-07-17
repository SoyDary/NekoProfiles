package me.nya.Objects;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.nya.NekoProfiles;

public class GenderProfile { 
	private NekoProfiles plugin = NekoProfiles.getPlugin(NekoProfiles.class);
	private String gender;
	private Player p;
	private File profileFile;
	private FileConfiguration config;
	
	public GenderProfile(String uuid) {
		this.p = Bukkit.getPlayer(UUID.fromString(uuid));
		this.profileFile = new File(plugin.getDataFolder(), "profiles/"+uuid+".yml");
		this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.profileFile);
		if(p != null) {
			String gender = "default";
			this.gender = gender;
			for(String str : plugin.getGenderList()) {		
				if(!str.equals("default")) {
					String placeholder = plugin.getConfig().getString("Genders."+str+".trigger_placeholder").split(";")[0];
					String condition = plugin.getConfig().getString("Genders."+str+".trigger_placeholder").split(";")[1];
					if(PlaceholderAPI.setPlaceholders(p, placeholder).contains(condition)) {
						gender = str;
						this.gender = gender;
						break;				
					}					
				}
				
			}
			
		} else {
			if(config.getString("gender") != null) {
			    this.gender = config.getString("gender");
			} else {
				this.gender = "default";
			}
			
		}
		
		
		
	}    

	public String getDisplayName() {
		return plugin.getConfig().getString("Genders."+gender+".displayName"); 
		
	}
	public String getName() {
		return gender;
		
	}
	public String getTag(String id) {
		List<String> genderTags = plugin.getConfig().getStringList("Genders."+gender+".tags");
		String tag = "";
		for(String str : genderTags) {
			String identiffer = str.split(";")[0];
			if(identiffer.equalsIgnoreCase(id)) {
				tag = str.split(";")[1];
				break;		
			}	
		}
		return tag;
		
		
	}
	/*
	private void saveData() {			
		try {		
			this.config.save(this.profileFile);			
			} catch (IOException e) {
			  e.printStackTrace();
			}
	}
	*/
	

}
