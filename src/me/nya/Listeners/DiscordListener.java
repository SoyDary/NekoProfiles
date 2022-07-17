package me.nya.Listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.nya.NekoProfiles;
import me.nya.Objects.NekoProfile;

public class DiscordListener implements Listener{
	private NekoProfiles plugin = NekoProfiles.getPlugin(NekoProfiles.class);
	DiscordSRV discord = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");

	/*
	@Subscribe
	public void onDiscordMessagee(DiscordGuildMessagePreProcessEvent e) {
    	if(e.getChannel().equals(discord.getMainTextChannel())) {
    		String msg = plugin.getUtils().color("&4[&cDiscord :v&4] &7"+e.getAuthor().getName()+" ("+e.getAuthor().getIdLong()+"): &f"+e.getMessage().getContentDisplay());
    		Bukkit.broadcast(msg, "*");
    		e.setCancelled(true);
    	}
    	

    	
    }
    */
	
    @Subscribe
	public void onDiscordMessage(DiscordGuildMessageReceivedEvent e) {
	    if (!e.getMessage().isEdited()) {   
	    	User user = e.getAuthor();
		    String message = e.getMessage().getContentDisplay();
		    if (message.toLowerCase().startsWith("k!profile ") && !message.toLowerCase().equals("k!profile")) { 
		    	String name = message.split(" ")[1];
		    	if(name == null) name = "-";
		    	List<User> members = e.getMessage().getMentionedUsers();
		    	if(name.startsWith("@") && members != null) {
	    			User mentioned = members.get(0);
	    			String uuid = plugin.getDataManager().getUUIDFromDiscordID(mentioned.getId()); 	 
	    			if(uuid != null) {
	    				if(plugin.getDataManager().isExactUser(uuid)) {
			    			NekoProfile profile = new NekoProfile(uuid);
			    			updateIfOnline(profile);
			    			DiscordUtil.queueMessage(e.getChannel(), profile.getDiscordProfileMessage());    
	    				} else {
	    					DiscordUtil.queueMessage(e.getChannel(), "<:gray_prisma:862570818621931531> `No se encontro ningún perfil registrado con este nombre`");
	    				}
	    				
	    			} else {
	    				DiscordUtil.queueMessage(e.getChannel(), "<:red_prisma:862570818676850738> `"+mentioned.getName()+" no tiene una cuenta de minecraft vinculada`");
	    			}

		    		
		    		} else {
						NekoProfile otherProfile = null;
						for(String str : plugin.getDataManager().getUsers()) {
							if(str.equalsIgnoreCase(name)) {
								otherProfile = new NekoProfile(plugin.getDataManager().users.getString(str));
								break;
							}
						}
						if(otherProfile != null) {
							updateIfOnline(otherProfile);
							DiscordUtil.queueMessage(e.getChannel(), otherProfile.getDiscordProfileMessage());  
							
						} else {
				    		DiscordUtil.queueMessage(e.getChannel(), "<:red_prisma:862570818676850738> `No se encontró ningún jugador registrado con este nombre.`");
						}
		    		}
		    			
		    		}

    

		    if(message.toLowerCase().equals("k!profile")) {
		    	String uuid = plugin.getDataManager().getUUIDFromDiscordID(user.getId());
		    	if(uuid != null) {		 
		    		if(plugin.getDataManager().isExactUser(uuid)) {
		    			NekoProfile profile = new NekoProfile(uuid);
		    			updateIfOnline(profile);
		    			DiscordUtil.queueMessage(e.getChannel(), profile.getDiscordProfileMessage());    
		    		} else {
		    			DiscordUtil.queueMessage(e.getChannel(), "<:gray_prisma:862570818621931531> `No hay ningún perfil creado con tu nombre... Conectate al servidor para poder crear uno.`");   
		    		}
		    	} else {
		    		DiscordUtil.queueMessage(e.getChannel(), "<:yellow_prisma:862570818768338994> ¡No tienes ninguna cuenta de minecraft vinculada. Usa `k!profile <nombre>` o vincula tu cuenta!"); 
		    	}
		    	
		    }
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
    /*
    @Subscribe
	public void onDiscordGuildMessageReceivedEvente(DiscordGuildMessageReceivedEvent e) {
	    if (!e.getMessage().isEdited()) {   	
		    String message = e.getMessage().getContentDisplay();
		    if (message.toLowerCase().startsWith("k!team")) { 	
		        DiscordManager.teamCommand(e.getAuthor(), e.getChannel(), e.getMessage(), e.getMember());
		      } else {
		    	  String l = message.toLowerCase();		    	  	       
		        if (DiscordManager.keys.containsKey(l)) {
			          String key = (String)DiscordManager.keys.get(l);
			          DiscordManager.sendDiscord(e.getChannel(), e.getMessage(), key);
		        }
		      }
	    }
    }
    */
}