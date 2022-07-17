package me.nya.API;


import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import net.kokoricraft.nekocore.managers.Core;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.nya.NekoProfiles;
import me.nya.Objects.NekoProfile;

public class PlaceholderAPI extends PlaceholderExpansion {
	DiscordSRV discord = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");
	JDA jda = discord.getJda();
	
    private NekoProfiles plugin;
  

    public PlaceholderAPI(NekoProfiles plugin) {
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
        return "profile";
    }


    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }
  
    
    @Override
    public String onPlaceholderRequest(Player p, String id) {
        	String nombre = StringUtils.substringBetween(id, "{", "}");
        	String tag = id.split("}")[1].replaceAll("_", "");
        	Core core = new Core();
        	if(tag == null) tag = "";
        	String uuid = null;
        	//discord-var
    		if(tag.equalsIgnoreCase("discordPrefix")) {
    			List<Member> members = plugin.getDiscord().getMainGuild().getMembersByName(nombre, false);	
    			if(members.size() == 0) return "&7 "+nombre+" &8»"; 
    			Member member = null;
    			int matches = 0;
    			for(Member mem : members) {
    				if(plugin.getDiscord().getAccountLinkManager().getLinkedAccounts().containsKey(mem.getId())) {
    					member = mem;
    					matches++;
    					
    				}
    			}
    			if(member == null) return "&7 "+nombre+" &8»"; 
    			if(matches > 1) return "&7 "+nombre+" &8»"; 
    			String uuidFromDC = getUUIDFromDiscordID(member.getUser().getId()); 
    			if(uuidFromDC != null) {
    				NekoProfile profileFromID = new NekoProfile(uuidFromDC);
    				if(profileFromID.exist()) {
    	    			updateIfOnline(profileFromID);			
    	    			return plugin.getUtils().color(getChatPrefix(profileFromID.getLastPrefix())+" "+plugin.getUtils().color(profileFromID.getPAPIvalue("name_color"))+profileFromID.getUserName()+" §8"+plugin.getUtils().color(profileFromID.getPAPIvalue("second_color"))+"»");
    				} else {
    					return "&7 "+nombre+" &8»"; 
    				}
    				
    			} else {
    				return "&7 "+nombre+" &8»"; 
    			}
    			
    			}
    		if(tag.equalsIgnoreCase("discordprefix-tag")) {
    			if(nombre.split("#").length == 1) {
    				String uuidd = plugin.getDataManager().getUUIDFromName(nombre);
    				if(uuidd == null) return null;
    				NekoProfile profilename = new NekoProfile(uuidd);
    				return getChatPrefix(profilename.getLastPrefix())+profilename.getPAPIvalue("name_color")+profilename.getUserName()+" &8"+profilename.getPAPIvalue("second_color")+"»";
    			}
    			Member member = discord.getMainGuild().getMemberByTag(nombre);
    			if(member == null) return "&7 "+nombre+" &8»"; 
    			String uuidFromDC = getUUIDFromDiscordID(member.getUser().getId()); 
    			if(uuidFromDC != null) {
    				NekoProfile profileFromID = new NekoProfile(uuidFromDC);
    				if(profileFromID.exist()) {
    	    			updateIfOnline(profileFromID);			
    	    			return getChatPrefix(profileFromID.getLastPrefix())+" "+profileFromID.getPAPIvalue("name_color")+profileFromID.getUserName()+" &8"+profileFromID.getPAPIvalue("second_color")+"»";
    				} else {
    					return "&7 "+nombre+" &8»"; 
    				}
    				
    			} else {
    				return "&7 "+nombre+" &8»"; 
    			}
    			
    			}
    		//online
        	if(plugin.getDataManager().isExactUser(nombre) || nombre.equalsIgnoreCase("me")) {
        		if(nombre.equalsIgnoreCase("me")) {
        			uuid = p.getUniqueId().toString();
        		} else {
        			if(nombre.length() > 30) uuid = nombre;	else uuid = plugin.getDataManager().users.getString(nombre);
        		}
     		
        		NekoProfile profile = new NekoProfile(uuid);
        		if(tag.equalsIgnoreCase("skinID")) {
        			if(profile.getSkinID() == null) {
        				return uuid;
        			} else {
        				return profile.getSkinID();
        			}
        		} 
        		
        		if(tag.startsWith("gender")) {
        			String gender_element = tag.split(":")[1].split(";")[0];
        			if(gender_element.equalsIgnoreCase("tag")) {
        				String gender_tag = tag.split(":")[1].split(";")[1];
        				return profile.getGender().getTag(gender_tag);			
        			} else {
            			if(gender_element.equalsIgnoreCase("displayName")) {
            				return profile.getGender().getDisplayName();	
            			} else {
                			if(gender_element.equalsIgnoreCase("name")) {
                				return profile.getGender().getName();	
                			} else {
                				return "unknown";
                			}
            			}
        			}
        			
        		}
        		
        		if(tag.equalsIgnoreCase("uwu")) {
        			return "§djaja";
        		} else {
        			return null;
        		}
        	} else {

        		
        		if(tag.equalsIgnoreCase("skinID")) {
            		String nullUUID = core.getPlayerUUID(nombre);
            		if(nullUUID != null) {
            			if(plugin.getUtils().isPremium(nullUUID)) {
            				return nullUUID;
            				
            			} else {
            				return nombre;
            			}
            		} else {
            			return nombre;
            		}
        		}
        		if(tag.startsWith("gender")) {
        			String gender_element = tag.split(":")[1].split(";")[0];
        			if(gender_element.equalsIgnoreCase("tag")) {
        				String gender_tag = tag.split(":")[1].split(";")[1];
        				List<String> genderTags = plugin.getConfig().getStringList("Genders.default.tags");
        				String g_tag = "";
        				for(String str : genderTags) {
        					String identiffer = str.split(";")[0];
        					if(identiffer.equalsIgnoreCase(gender_tag)) {
        						g_tag = str.split(";")[1];
        						break;		
        					}	
        				}
        				return g_tag;		
        			}
        			if(gender_element.equalsIgnoreCase("displayName")) {
        				return plugin.getConfig().getString("Genders.default.displayName");	
        			}
           			if(gender_element.equalsIgnoreCase("name")) {
        				return "default";
        			} else {
        				 return "unknown"; 
        			} 
     
        			
        		} else {
        			return null;
        		}
        	}
    
    }
	public String getChatPrefix(String str) {
		String sp = str.length() > 10 ? " " : "";
		if(str.toLowerCase().contains("booster")) return " &#ff4da6[&#ffb3ffBooster&#ff4da6]&#ffccff";
		if(str.toLowerCase().contains("veteran")) return "&#80ffaa";
		if(str.toLowerCase().contains("miembr") && str.contains("+")) return "&#80ffff";
		if(str.toLowerCase().contains("miembr")) return "&7";
		return sp+str;

		
	}
	public String getUUIDFromDiscordID(String discordID) {
		Map<String, UUID> accounts = discord.getAccountLinkManager().getLinkedAccounts();
		if(accounts.containsKey(discordID)) {
			return accounts.get(discordID).toString();
			
		} else {
			return null;
		}
	}
    public void updateIfOnline(NekoProfile profile) {
    	Player p = Bukkit.getPlayer(profile.getUniqueID());
    	if(p != null) {
    		profile.updateUserName();
    		profile.updateLastPrefix();
    		profile.updateSkinID();
    		profile.updateGender();
    		profile.updatePlaceholders();	
    	}

    	
    }
    
    
}
