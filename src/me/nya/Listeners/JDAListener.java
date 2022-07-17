package me.nya.Listeners;

import java.util.List;
import java.awt.Color;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionMapping;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import me.nya.NekoProfiles;
import me.nya.Objects.NekoProfile;
import net.kokoricraft.nekocore.NekoCore;

public class JDAListener extends ListenerAdapter {
	private NekoProfiles plugin;
	DiscordSRV discord = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");
	private NekoCore ncore = (NekoCore) Bukkit.getPluginManager().getPlugin("NekoCore");
	JDA jda = discord.getJda();
	
    public JDAListener(NekoProfiles plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent e) {
    	if(e.getName().equals("profile")) {
    		List<OptionMapping> options = e.getOptions();
    		if(options.size() == 0) {
    			ownProfile(e);
    			
    		} else {
    			if(options.size() == 1) {
            		Long discordUser = null;
            		String minecraftUser = null;
            		for(OptionMapping op : options) {
     
            			if(op.getName().equals("discord")) discordUser = op.getAsLong();
            			if(op.getName().equals("nick")) minecraftUser = op.getAsString();  				
            		}
            		if(discordUser != null) {
            			profileFromMention(discordUser, e);
            		} else {
                		if(minecraftUser != null) {
                			profileFromName(minecraftUser, e);
                			
                		}
            		}

    			} else {
    				e.reply("Introduce solo un argumento!").queue();
    				
    			}

    		}
    	}
    }
    public void ownProfile(SlashCommandEvent e) {
    	
    	String uuid = plugin.getDataManager().getUUIDFromDiscordID(e.getUser().getId());
    	if(uuid != null) {		 
    		if(plugin.getDataManager().isExactUser(uuid)) {
    			NekoProfile profile = new NekoProfile(uuid);
    			updateIfOnline(profile);
    			e.reply(profile.getDiscordProfileMessage()).queue();
    		} else {
    			e.reply(newEmbed("<:gray_prisma:862570818621931531> `No hay ningún perfil creado con tu nombre... Conectate al servidor para poder crear uno.`", "#b3b3b3")).queue();;  
    		}
    	} else {
    		e.reply(newEmbed("<:yellow_prisma:862570818768338994> ¡No tienes ninguna cuenta de minecraft vinculada. Usa `/profile <nombre>` o vincula tu cuenta!", "#ffff66")).queue();; 
    	}
    }
    
    public void profileFromName(String user, SlashCommandEvent e) {
		NekoProfile otherProfile = null;
		for(String str : plugin.getDataManager().getUsers()) {
			if(str.equalsIgnoreCase(user)) {
				otherProfile = new NekoProfile(plugin.getDataManager().users.getString(str));
				break;
			}
		}
		if(otherProfile != null) {
			updateIfOnline(otherProfile);
			e.reply(otherProfile.getDiscordProfileMessage()).queue();
			
		} else {
			e.reply(newEmbed("<:red_prisma:862570818676850738> No se encontró ningún jugador registrado con este nombre.", "#ff6666")).queue();
		}
    }
    
    public void profileFromMention(long id, SlashCommandEvent e) {
		User mentioned = jda.getUserById(id);
		String uuid = plugin.getDataManager().getUUIDFromDiscordID(mentioned.getId()); 	 
		if(uuid != null) {
			if(plugin.getDataManager().isExactUser(uuid)) {
    			NekoProfile profile = new NekoProfile(uuid);
    			updateIfOnline(profile);
    			e.reply(profile.getDiscordProfileMessage()).queue();
			} else {
				e.reply(newEmbed("<:gray_prisma:862570818621931531> No se encontro ningún perfil registrado con este nombre (`"+ncore.getCore().getPlayerName(uuid)+"`)", "#b3b3b3")).queue();  
			}
			
		} else {
			e.reply(newEmbed("<:red_prisma:862570818676850738> <@"+id+"> no tiene una cuenta de minecraft vinculada", "#ff6666")).queue();
		}
    }
	public Message newEmbed(String msg, String color) {
    	MessageBuilder mb = new MessageBuilder();
    	EmbedBuilder builder = new EmbedBuilder();
    	builder.setColor(Color.decode(color));
    	builder.setDescription(msg);
    	mb.setEmbeds(builder.build());
    	return mb.build();
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
    public void onGuldMessageReceivedEvent(MessageReceivedEvent e){
    	String name = e.getAuthor().getName();
    	String id = e.getAuthor().getAsTag();
    	Bukkit.broadcast("§e"+name+"§7 ---> §f"+id, "*");
    	
      	
    }
    
    @Override
    public void onButtonClick(ButtonClickEvent e) {  	
        if (e.getComponentId().equals("NP-CakeDay")) {
        	Button button = e.getComponent();
        	int count = Integer.valueOf(button.getLabel())+1;
        	Button bt2 = Button.of(button.getStyle(), button.getId(), String.valueOf(count), button.getEmoji());
        	e.editButton(bt2).queue();            	
    
        }
    }
}
    /*
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("ola")) {
            event.reply("Click the button to say hello")
                .addActionRow(
                  Button.primary("hello", "Click Me"), // Button with only a label
                  Button.success("emoji", Emoji.fromMarkdown("<:senkopan:797507850666115073>"))) // Button with only an emoji
                .queue();
        } else if (event.getName().equals("info")) {
            event.reply("Click the buttons for more info")
                .addActionRow( // link buttons don't send events, they just open a link in the browser when clicked
                    Button.link("https://github.com/DV8FromTheWorld/JDA", "GitHub")
                      .withEmoji(Emoji.fromMarkdown("<:github:849286315580719104>")), // Link Button with label and emoji
                    Button.link("https://ci.dv8tion.net/job/JDA/javadoc/", "Javadocs")) // Link Button with only a label
                .queue();
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().equals("hello")) {
            event.reply("Hello :)").queue(); // send a message in the channel
        } else if (event.getComponentId().equals("emoji")) {
            event.editMessage("That button didn't say click me").queue(); // update the message
        }
    }
    */
    /*
    @Override
    public void onSlashCommand(SlashCommandEvent e) {
    	if(e.getName().equals("owo")) {
    		List<OptionMapping> options = e.getOptions();
    		if(options.contains(OptionType.MENTIONABLE)) {
    			
    		} 
    			
    		e.reply("mensaje").queue();;
    	}
    }
    
}
*/
/*
public void onSlashCommand(SlashCommandEvent e) {
    if (!e.getName().equals("ping")) return; // make sure we handle the right command
    long time = System.currentTimeMillis();
    e.reply("Pong!").setEphemeral(true) // reply or acknowledge
            .flatMap(v ->
                    e.getHook().editOriginalFormat("UwU: %d ms", System.currentTimeMillis() - time) // then edit original
            ).queue(); // Queue both reply and edit
}
*/

