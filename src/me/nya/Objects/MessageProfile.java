package me.nya.Objects;


import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;

import NekosPlugins.NekoTeams.Teams;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emote;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed.Field;
import me.clip.placeholderapi.PlaceholderAPI;
import me.nya.NekoProfiles;
import net.kokoricraft.nekocore.managers.Core;
import net.kokoricraft.nekotags.NekoTags;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
@SuppressWarnings("deprecation")
public class MessageProfile {
	NekoProfile profile;
	CakeDay cumple;
	private NekoProfiles plugin = NekoProfiles.getPlugin(NekoProfiles.class);
	private NekoTags nekotags = (NekoTags) Bukkit.getPluginManager().getPlugin("NekoTags");
	
	public MessageProfile(NekoProfile profile) {
		this.profile = profile;
		this.cumple = new CakeDay(profile.getUniqueID().toString());		
	}
	public TextComponent getTextComponent() {
		TextComponent end = new TextComponent();
		end.addExtra(bar());
		end.addExtra("\n");
		end.addExtra(line1());
		end.addExtra("\n\n");
		end.addExtra(line2());
		end.addExtra("\n");
		end.addExtra(line3()); //Tiempo jugado
		if(plugin.is_nekoteams) {
			end.addExtra("\n");
			end.addExtra(line4()); 		
		}
		end.addExtra("\n");
		end.addExtra(line5()); //Discord
		end.addExtra("\n");
		end.addExtra(line_bday());
		end.addExtra("\n");
		end.addExtra(line6());
		end.addExtra("\n");
		end.addExtra(line7());
		end.addExtra("\n");
		end.addExtra(line8());
		end.addExtra("\n");
		end.addExtra(line9());
		end.addExtra("\n\n");
		end.addExtra(line10());
		if(plugin.is_actionschat) {
			end.addExtra("\n\n");
			end.addExtra(line_marry()); //Pareja
		}
		end.addExtra(bar());
		return end;
		
	}
	public Message getDiscordMessage() {
	    plugin.getUtils();
		StringBuffer sb = new StringBuffer();	
		String description = "";	
		sb.append("\n~~----------------------------------------------~~");	
		
		if(profile.isOnline()) 
			sb.append(nc("\n<:status_online:928557557126148096> **Estado: ** Conectad"+profile.getGender().getTag("suffix")+" `"+profile.getLastSeenFormated()+"`"));			
		 else 
			 sb.append(nc("\n<:status_offline:928557592953897010> **Estado: ** Desconectad"+profile.getGender().getTag("suffix")+" `"+profile.getLastSeenFormated()+"`"));
		
		sb.append(nc("\n<:mc_clock:928558759243022358> **Tiempo jugado:** "+playTime()));
		if(plugin.is_nekoteams) {
			if(profile.getTeam() != null) {
				String prefix = profile.getTeam().getPrefix().replaceAll("#", "&#");
				String display_name = profile.getTeam().getDisplayName().replaceAll("#", "&#");
				sb.append(nc("\n<:birch_sign:928564738227568660> **Equipo:** "+prefix+display_name));
			} else {
				sb.append(nc("\n<:birch_sign:928564738227568660> **Equipo:** *ninguno*"));
			}
		}
		if(profile.getDiscordID() != 0) 
			sb.append("\n<:discord:928554190983270440> **Discord:** <@"+profile.getDiscordID()+"> "+(profile.isBooster() ? "<a:booster:949002324838350959>" : ""));
		else
			sb.append("\n<:discord:928554190983270440> **Discord:** *ninguno*");
		if(cumple.bday_date != null) {
			int day = cumple.day;
			String month = cumple.monthString.toLowerCase();
			sb.append("\n:cake: **Cumpleaños:** "+day+" de "+month);
		} else {
			sb.append("\n:cake: **Cumpleaños:** *indefinido*");
		}
		sb.append(nc("\n<:two_hearths:928560806612840459> **Género:** "+profile.getGender().getDisplayName()));
		String logros = profile.getPAPIvalue("logros");
		if(logros.equals("")) { logros = "0"; }
		sb.append(nc("\n<:enchanted_apple:928560104079507486> **Logros:** "+logros+"/95"));
		sb.append(nc("\n<:nether_star:928563361199820830> **Primer registro:** "+profile.getFirstJoin()));
		StringBuffer sb_ranks = new StringBuffer();
		List<String> groups = profile.getUserGroups();
		String s = "";
		if(groups.size() > 1) s = "s";
		sb_ranks.append(":label: **Rango"+s+":** ");
		if(groups.size() != 0){
		    int i = 0;
			for(String str : profile.getUserGroups()) {
				i++;		
				if(i+1 == 7) sb_ranks.append("  ");
				if(groups.size() != 1) {
					if(!str.equals("default")) {
						if(plugin.getConfig().getString("Groups.group_aliases."+str) != null) {
							sb_ranks.append(nc(plugin.getConfig().getString("Groups.group_aliases."+str)+"  "));
						} else {
							sb_ranks.append(nc(plugin.getLuckPermsAPI().getGroupManager().getGroup(str).getCachedData().getMetaData().getPrefix()+" "));				
						}
						
					}
				} else {
					if(plugin.getConfig().getString("Groups.group_aliases."+str) != null) {
						sb_ranks.append(nc(plugin.getConfig().getString("Groups.group_aliases."+str)+"  "));
					} else {
						sb_ranks.append(nc(plugin.getLuckPermsAPI().getGroupManager().getGroup(str).getCachedData().getMetaData().getPrefix()+" "));				
					}
				}
			}
			} else {
				sb_ranks.append("[Miembro]");
				
			}
		sb.append("\n"+sb_ranks.toString());
		sb.append("\n~~----------------------------------------------~~");
		description = sb.toString(); sb.isEmpty();
		StringBuffer stats_builder_1 = new StringBuffer();
		stats_builder_1.append("<:diamond_pickaxe:928544064893235231> **Bloques minados:** "+nfs(profile.getPAPIvalue("stat_mine_block")));
		stats_builder_1.append("\n<:diamond:928544307164622878> **Diamantes minados:** "+nfs(profile.getPAPIvalue("diamantes")));
		stats_builder_1.append("\n<:crafting_table:928544821436633088> **Items Crafteados:** "+nfs(profile.getPAPIvalue("stat_craft_item")));
		stats_builder_1.append("\n<:diamond_sword:928545196218679348> **Mobs asesinados:** "+nfs(profile.getPAPIvalue("stat_mob_kills")));
		stats_builder_1.append("\n<:wheat:928545741067145256> **Animales criados:** "+nfs(profile.getPAPIvalue("stat_animal_bred")));
		Field stats_field1 = new Field("<:stats:928549698850144327>  __**Estadisticas:**__", stats_builder_1.toString(), true);
		StringBuffer stats_builder_2 = new StringBuffer();
		stats_builder_2.append("<:skeleton_skull:928546954638676048> **Muertes totales:** "+nfs(profile.getPAPIvalue("stat_deaths")));
		stats_builder_2.append("\n<:noteblock:928547797278552095> **Ticks musicales:** "+nfs(profile.getPAPIvalue("stat_noteblock_tuned")));
		stats_builder_2.append("\n<:emerald:928548214532096080> **Tradeos con aldeanos:** "+nfs(profile.getPAPIvalue("stat_villagers")));
		stats_builder_2.append("\n<:expbottle:928548744901845032> **Niveles de experiencia:** "+nfs(profile.getPAPIvalue("experiencia")));
		stats_builder_2.append("\n<:ench_tabble:928549225480978462> **Items encantados:** "+nfs(profile.getPAPIvalue("stat_item_enchanted")));
		Field stats_field2 = new Field("•••", stats_builder_2.toString(), true);
		
		
		Field tags_field = null;
		if(plugin.is_nekotags) {
			if(nekotags.getData().getTags(profile.getUniqueID().toString()).size() != 0) {
				StringBuffer tags_builder = new StringBuffer("");	
				Guild guild = plugin.getDiscord().getJda().getGuildById("951279185043816509"); 
				for(String key : nekotags.getData().getTags(profile.getUniqueID().toString())) {
					Emote emote = guild.getEmotesByName(key, false).get(0);
					String esmote = "<"+(emote.isAnimated() ? "a:": ":")+emote.getName()+":"+emote.getId()+">";
					tags_builder.append("*"+esmote+"* ");			
					
				}
				tags_field = new Field("<:name_tag:928562589770862654> __**Tags:**__", tags_builder.toString(), false);
				
			}
		}


		
		String iconUrl = "https://cdn.discordapp.com/attachments/727213886507581500/802927508328153108/logo_tarde.png";
		if(profile.getExtraData("embed_url") != null) iconUrl = profile.getExtraData("embed_url");
		String url = "https://es.namemc.com/profile/"+profile.getUniqueID().toString();
		String ThumbnailURL = "https://mc-heads.net/body/"+profile.getSkinID()+"/50/left.png";
		MessageEmbed embed = null;
		EmbedBuilder builder = new EmbedBuilder();
		MessageBuilder mb = new MessageBuilder();
		builder.setThumbnail(ThumbnailURL);

		if(profile.isPremium()) {		
			builder.setAuthor(nc(profile.getLastPrefix())+" "+profile.getUserName()+" (Premium)", url, iconUrl);
		} else {
			builder.setAuthor(nc(profile.getLastPrefix())+" "+profile.getUserName(), null, iconUrl);
		}
		String colorString = profile.getPAPIvalue("second_color"); 
		if(colorString.equals("")) {
			colorString = "#ffff99";
		} else {
			if(colorString.length() == 2) {
				colorString = transformColorToRgb(profile.getPAPIvalue("second_color"));
			} 
		}
		Color color = Color.decode(colorString);
		builder.setColor(color);
		builder.setDescription(description);
		builder.addField(stats_field1);
		builder.addField(stats_field2);
		if(tags_field != null) builder.addField(tags_field);
		if(plugin.is_actionschat) {
			if(profile.getCouple(true) != null) {
			    String coupleAvatar = PlaceholderAPI.setPlaceholders(null, "https://mc-heads.net/avatar/%profile_{"+profile.getCouple(true)+"}_skinID%/30.png");
			    String footerTxT = "Casad"+profile.getGender().getTag("suffix")+" con "+profile.getCouple(true);
			    builder.setFooter(footerTxT, coupleAvatar);
			}
		}


		embed = builder.build();
        mb.setEmbeds(embed);
        Message msg = mb.build();
        return msg;
		
	}
	private String transformColorToRgb(String value) {		
		 String str = "#ffffff";
	      if (value.equals("&0") || value.equals("§0"))
	    	  str = "#000000";
	      if (value.equals("&1") || value.equals("§1"))
	    	  str = "#0000aa";
	      if (value.equals("&2") || value.equals("§2"))
	    	  str = "#00aa00";
	      if (value.equals("&3") || value.equals("§3"))
	    	  str = "#00aaaa";
	      if (value.equals("&4") || value.equals("§4"))
	    	  str = "#aa0000";
	      if (value.equals("&5") || value.equals("§5"))
	    	  str = "#aa00aa";
	      if (value.equals("&6") || value.equals("§6"))
	    	  str = "#ffaa00";
	      if (value.equals("&7") || value.equals("§7"))
	    	  str = "#aaaaaa";
	      if (value.equals("&8") || value.equals("§8"))
	    	  str = "#555555";
	      if (value.equals("&9") || value.equals("§9"))
	    	  str = "#5555ff";
	      if (value.equals("&a") || value.equals("§a"))
	    	  str = "#55ff55";
	      if (value.equals("&b") || value.equals("§b"))
	    	  str = "#55ffff";
	      if (value.equals("&c") || value.equals("§c"))
	    	  str = "#ff5555";
	      if (value.equals("&d") || value.equals("§d"))
	    	  str = "#ff55ff";
	      if (value.equals("&e") || value.equals("§e"))
	    	  str = "#ffff55";
	      if (value.equals("&f") || value.equals("§f"))
	    	  str = "#ffffff";
	      return str;
	  }
	private String nc(String str) {
    	
	    return ChatColor.stripColor(plugin.getUtils().color(str));
	}
	private TextComponent bar() {
		TextComponent end = new TextComponent("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		String color = profile.getPAPIvalue("chat_color");
		end.setColor(translateColor(color));
		return end;
	}
	private TextComponent line1() {
		TextComponent end = getText("&#1affc6» &7Perfil de "); 
		TextComponent name_prefix = getText(profile.getLastPrefix()+" "+profile.getUserName());
		ComponentBuilder bn = new ComponentBuilder();
		bn.append("§7"+profile.getUniqueID().toString());
		HoverEvent hn = new HoverEvent(HoverEvent.Action.SHOW_TEXT, bn.create());
		name_prefix.setHoverEvent(hn);
		name_prefix.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, profile.getUniqueID().toString())); 
		end.addExtra(name_prefix);	
		if(!plugin.getDataManager().premiums.containsKey(profile.getUniqueID())) plugin.getDataManager().premiums.put(profile.getUniqueID(), profile.isPremium());
		if(plugin.getDataManager().premiums.get(profile.getUniqueID())) {
			if(plugin.getDataManager().name_history_component.containsKey(profile.getUniqueID())) {
				end.addExtra(plugin.getDataManager().name_history_component.get(profile.getUniqueID()));
			} else {
				end.addExtra(premiumButton());
				plugin.getDataManager().name_history_component.put(profile.getUniqueID(), premiumButton());
			}
		}
		TextComponent two = getText("&7:");
		end.addExtra(two);
		
		 return end;
	}
	public TextComponent premiumButton() {
		TextComponent premium = getText(" &#ffff00(&#ffd966Premium&#ffff00)");
		ComponentBuilder cba = new ComponentBuilder();
		cba.append(getText("&#ffff00&lHistorial de nombres:"));
		cba.append(new TextComponent(getText("\n&#ffff99&m                                \n")));
		List<String> nameHistory = plugin.getDataManager().nameHistory.getOrDefault(profile.getUniqueID(), profile.getNameHistory());
		if(!plugin.getDataManager().nameHistory.containsKey(profile.getUniqueID())) {
			plugin.getDataManager().nameHistory.put(profile.getUniqueID(), nameHistory);
		} else {
		}
        for(String str : nameHistory) {
        	String name = str.split("-")[0];
        	long time = Long.parseLong(str.split("-")[1]);	
        	if(time != 0) {
        		TextComponent line = getText("§e"+name+" §7"+diferencia(time));
        		line.addExtra("\n");
        		cba.append(line);
        	} else {
        		TextComponent line = getText("§e"+name);	
        		line.addExtra("\n");
        		cba.append(line);	        		
        	}
        }
        cba.append("\n");
	    cba.append(getText("&7&oClic para ver en línea"));
	    HoverEvent ev1 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cba.create());

	    
	    premium.setHoverEvent(ev1);
	    premium.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://es.namemc.com/profile/"+profile.getUniqueID()));  
	    return premium;
		
	}
	
	private TextComponent line2() {
		TextComponent end = getText("   "+profile.getPAPIvalue("second_color").replaceAll("#", "&#")+"↳ &7Estado: ");

		if(profile.isOnline()) {
			TextComponent online = getText("&#a6ff4dConectad"+profile.getGender().getTag("suffix")+" &#ccff99"+profile.getLastSeenFormated());
			end.addExtra(online);
			
		} else {
			TextComponent offline = getText("&#ff3333Desconectad"+profile.getGender().getTag("suffix")+" &#ff9999"+profile.getLastSeenFormated());
			end.addExtra(offline);
			
		}
		return end;
	}
	private TextComponent line3() {
		TextComponent end = getText("   "+profile.getPAPIvalue("second_color").replaceAll("#", "&#")+"↳ &7Tiempo jugado: "+playTime());
		return end;		
	}	
	private TextComponent line4() {
		Teams team = profile.getTeam();
		Core core = new Core();
		TextComponent end = getText("   "+profile.getPAPIvalue("second_color").replaceAll("#", "&#")+"↳ §7Equipo: ");
		if(team != null) {
			String prefix = team.getPrefix().replaceAll("#", "&#");
			String display_name = team.getDisplayName().replaceAll("#", "&#");
			TextComponent tim = new TextComponent();
			tim.addExtra(getText(prefix));
			tim.addExtra(getText(display_name));
			ComponentBuilder cba = new ComponentBuilder();
			tim.addExtra(new TextComponent(" §7#"+team.getName()));
			cba.append(new TextComponent(getText(translateColor(team.getSignColor())+"» "+team.getColor()+"Miembros del equipo "+translateColor(team.getSignColor())+"«\n")));
			String leader = team.getLeader();
			int i = 0;
			for(String member :team.getMembers()) {
				i++;
				if(member.equals(leader)) {
					TextComponent t1 = getText("   §8↳ &#cccccc"+core.getPlayerName(member)+" &#ffff00(Líder)");
					cba.append(t1);
				} else {
					TextComponent t1 = getText("   §8↳ &#cccccc"+core.getPlayerName(member));
					cba.append(t1);
				}
				if(i < team.getMembers().size()) cba.append("\n");
				
					
			}
			cba.append(new TextComponent(getText("\n\n"+translateColor(team.getSignColor())+"Fecha de creación: "+team.getColor()+team.getDate())));
			HoverEvent ev1 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cba.create());
			tim.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tb "+team.getName()));  
			tim.setHoverEvent(ev1);
			end.addExtra(tim);

			
		} else {
			end.addExtra(getText("&#cccccc&oninguno"));
		}
		return end;
		
	}
	private TextComponent line5() {
		TextComponent end = getText("   "+profile.getPAPIvalue("second_color").replaceAll("#", "&#")+"↳ §7Discord: ");
		String discord = profile.getDiscordTag();
		if(!discord.equals("")) {
			String name = discord.split("#")[0];
			String id = discord.split("#")[1];
			TextComponent dc = new TextComponent();
			TextComponent dc_name = new TextComponent("@"+name);
			dc_name.setColor(ChatColor.of("#3399ff"));
			TextComponent dc_ash = new TextComponent("#");
			dc_ash.setColor(ChatColor.of("#0073e6"));
			TextComponent dc_id = new TextComponent(id);
			dc_id.setColor(ChatColor.of("#3399ff"));
			dc.addExtra(dc_name);
			dc.addExtra(dc_ash);
			dc.addExtra(dc_id);
			end.addExtra(dc);
			ComponentBuilder cba = new ComponentBuilder();
			cba.append(getText("&7&oClic para copiar"));
			HoverEvent ev1 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cba.create());
			end.setHoverEvent(ev1);
			end.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, discord));  
					
		} else {
			end.addExtra(getText("&#cccccc&oninguno"));
		}
		return end;
		
		
	}
	private TextComponent line_bday() {
		String bday = "&#cccccc&oindefinido";
		if(cumple.bday_date != null) {
			int day = cumple.day;
			String month = cumple.monthString.toLowerCase();
			bday = "&#e699ff"+day+" de "+month;
		}
		TextComponent end = getText("   "+profile.getPAPIvalue("second_color").replaceAll("#", "&#")+"↳ §7Cumpleaños: ");
		end.addExtra(getText(bday));
		return end;
		
	}
	private TextComponent line6() {
		String display = profile.getGender().getDisplayName();
		TextComponent end = getText("   "+profile.getPAPIvalue("second_color").replaceAll("#", "&#")+"↳ §7Genero: ");
		end.addExtra(getText(display));
		return end;

		
	}
	private TextComponent line7() {
		String logros = profile.getPAPIvalue("logros");
		if(logros.equals("")) { logros = "0"; }
		TextComponent end = getText("   "+profile.getPAPIvalue("second_color").replaceAll("#", "&#")+"↳ §7Logros: ");
		end.addExtra(getText("&b"+logros+"&8/&b95"));
		return end;

		
	}
	private TextComponent line8() {
		TextComponent end = getText("   "+profile.getPAPIvalue("second_color").replaceAll("#", "&#")+"↳ §7Primer registro: ");
		end.addExtra(getText("&#ffe699"+profile.getFirstJoin()));
		return end;

		
	}
	private TextComponent line9() {
		List<String> groups = profile.getUserGroups();
		String s = "";
		if(groups.size() > 1) s = "s";
		TextComponent end = getText("   "+profile.getPAPIvalue("second_color").replaceAll("#", "&#")+"↳ §7Rango"+s+": ");

		if(groups.size() != 0){
	    int i = 0;
		for(String str : profile.getUserGroups()) {
			i++;
			if(i+1 == 7) end.addExtra(new TextComponent(getText("\n               ")));
			if(groups.size() != 1) {
				if(!str.equals("default")) {
					if(plugin.getConfig().getString("Groups.group_prefixes."+str) != null) {
						end.addExtra(getText(plugin.getConfig().getString("Groups.group_prefixes."+str)+" "));
					} else {
						end.addExtra(getText(plugin.getLuckPermsAPI().getGroupManager().getGroup(str).getCachedData().getMetaData().getPrefix()+" "));				
					}
					
				}
			} else {
				if(plugin.getConfig().getString("Groups.group_prefixes."+str) != null) {
					end.addExtra(getText(plugin.getConfig().getString("Groups.group_prefixes."+str)+" "));
				} else {
					end.addExtra(getText(plugin.getLuckPermsAPI().getGroupManager().getGroup(str).getCachedData().getMetaData().getPrefix()+" "));				
				}
			}
	


		}
		} else {
			end.addExtra(getText("&7[Miembro]"));
			
		}
		return end;

		
	}
	private TextComponent line10() {
		TextComponent end = new TextComponent("  ");
		TextComponent stats_button = getText("&#ff8c1a【&e🗡 &#ffcc99Estadisticas &e⛏ &#ff8c1a】");
		ComponentBuilder cbs = new ComponentBuilder();
		cbs.append(new TextComponent(getText("&8&m>-- ---[&eEstadisticas&8&m]---- --<")));		
		cbs.append(new TextComponent(getText("")));	
		cbs.append(new TextComponent(getText("\n&a⛏ &7Bloques minados: &#99FF99"+nfs(profile.getPAPIvalue("stat_mine_block")))));	
		cbs.append(new TextComponent(getText("\n&#1affff❖ &7Diamantes minados: &#B3FFEC"+nfs(profile.getPAPIvalue("diamantes")))));	
		cbs.append(new TextComponent(getText("\n&#bf8040⌂ &7Items crafteados: &#d9b38"+nfs(profile.getPAPIvalue("stat_craft_item")))));	
		cbs.append(new TextComponent(getText("\n&#FF3333🗡 &7Mobs asesinados: &#FF9999"+nfs(profile.getPAPIvalue("stat_mob_kills")))));	
		cbs.append(new TextComponent(getText("\n&#FF80FF❤ &7Animales criados: &#FFB3FF"+nfs(profile.getPAPIvalue("stat_animal_bred")))));	
		cbs.append(new TextComponent(getText("\n&#4D4D4D☠ &7Muertes totales: &#CCCCCC"+nfs(profile.getPAPIvalue("stat_deaths")))));	
		cbs.append(new TextComponent(getText("\n&#ff1a53♫ &7Ticks musicales: &#ffb3c6"+nfs(profile.getPAPIvalue("stat_noteblock_tuned")))));
		cbs.append(new TextComponent(getText("\n&#009900⬙ &7Tradeos con aldeanos: &#4DFF4D"+nfs(profile.getPAPIvalue("stat_villagers")))));	
		cbs.append(new TextComponent(getText("\n&#ffff00✭ &7Niveles de experiencia: &#ffff99"+nfs(profile.getPAPIvalue("experiencia")))));
		cbs.append(new TextComponent(getText("\n&#8c1aff✴ &7Items encantados: &#cc99ff"+nfs(profile.getPAPIvalue("stat_item_enchanted")))));
		HoverEvent evs = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cbs.create());
		stats_button.setHoverEvent(evs);	
		end.addExtra(stats_button);
		end.addExtra(new TextComponent(getText(" ")));
		TextComponent skin_button = getText("&#ff3366【&d✩ &#ff99ffSkin &d✩&#ff3366】");
		ComponentBuilder cbsk = new ComponentBuilder();
		cbsk.append(new TextComponent(getText("&7&oClic para copiar el link de la skin en el portapapeles")));
		cbsk.append(new TextComponent(getText("\n\n&ePuedes usar esta skin usando el comando &#ffff99/skin <link>")));
		HoverEvent evsk = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cbsk.create());
		skin_button.setHoverEvent(evsk);
		skin_button.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "http://textures.minecraft.net/texture/"+profile.getSkinID())); 
		end.addExtra(skin_button);
		end.addExtra(new TextComponent(getText(" ")));
		TextComponent msg_button = getText("&#00b3b3【&b✉ &#99ffffMensaje &b✉&#00b3b3】");
		ComponentBuilder bcmsg = new ComponentBuilder();
		bcmsg.append(new TextComponent(getText("&7&oClic para enviar un mensaje privado")));
		HoverEvent evmsg = new HoverEvent(HoverEvent.Action.SHOW_TEXT, bcmsg.create());
		msg_button.setHoverEvent(evmsg);	
		msg_button.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg "+profile.getUserName()+" ")); 
		if(!profile.isOnline()) {
			msg_button = getText("&#00b3b3【&b✉ &#99ffffCorreo &b✉&#00b3b3】");
			ComponentBuilder bcmsg2 = new ComponentBuilder();
			bcmsg2.append(new TextComponent(getText("&7&oEnvía un mensaje que "+profile.getUserName()+" podrá ver cuando se conecte")));
			HoverEvent evmsg2 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, bcmsg2.create());
			msg_button.setHoverEvent(evmsg2);	
			msg_button.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mail send "+profile.getUserName()+" ")); 	
		}
		end.addExtra(msg_button);
			
		return end;

		
	}
	private TextComponent line_marry() {
		String pareja = this.profile.getCouple(true);
		TextComponent end = new TextComponent();
		if(pareja != null) {
			end = getText("   &d♥ &eCasad"+this.profile.getGender().getTag("suffix")+" con &b"+pareja+" &d♥\n");		
		}
		return end;

		
	}
	private String playTime() {		
		int ticks = profile.getStatistic(Statistic.PLAY_ONE_MINUTE);
		int totalSeconds = ticks/20;
		int dias = totalSeconds / 86400;
	    int horas = totalSeconds % 86400 / 3600;
	    int minutos = totalSeconds % 86400 % 3600 / 60;
	    String playtime = "Tiempo";
	    if (dias < 1) {
	      playtime = "&a" + horas + "&eh &a" + minutos + "&em";
	    } else {
	      playtime = "&a" + dias + "&ed &a" + horas + "&eh &a" + minutos + "&em";
	    } 
	    return playtime;
	}
	private ChatColor translateColor(String str) {

		if(str.length() > 3) {
			return ChatColor.of(str);		
		} else {
			if(str.contains("0")) return ChatColor.BLACK;
			if(str.contains("1")) return ChatColor.DARK_BLUE;
			if(str.contains("2")) return ChatColor.DARK_GREEN;
			if(str.contains("3")) return ChatColor.DARK_AQUA;
			if(str.contains("4")) return ChatColor.DARK_RED;
			if(str.contains("5")) return ChatColor.DARK_PURPLE;
			if(str.contains("6")) return ChatColor.GOLD;
			if(str.contains("7")) return ChatColor.GRAY;
			if(str.contains("8")) return ChatColor.DARK_GRAY;
			if(str.contains("9")) return ChatColor.BLUE;
			if(str.contains("a")) return ChatColor.GREEN;
			if(str.contains("b")) return ChatColor.AQUA;
			if(str.contains("c")) return ChatColor.RED;
			if(str.contains("d")) return ChatColor.LIGHT_PURPLE;			
			if(str.contains("e")) return ChatColor.YELLOW;
			if(str.contains("f")) return ChatColor.WHITE;
			else return ChatColor.DARK_GRAY;
		}
	}
	private TextComponent getText(String text) {
	    String ttt = text;
	    if (ttt.contains("§x"))
	      ttt = removeRgb(ttt); 
	    String[] rgb = ChatColor.translateAlternateColorCodes('&', ttt).split(Pattern.quote("#"));
	    ArrayList<TextComponent> list = new ArrayList<>();
	    TextComponent end = new TextComponent("");
	    int count = 0;
	    byte b;
	    int i;
	    String[] arrayOfString1;
	    for (i = (arrayOfString1 = rgb).length, b = 0; b < i; ) {
	      String v = arrayOfString1[b];
	      if (count == 0) {
	        TextComponent tc0 = new TextComponent(v.replaceAll("&", "§"));
	        list.add(tc0);
	      } else {
	        Boolean isColor = Boolean.valueOf(true);
	        try {
	          String acolor = "#" + v.substring(0, 6);
	          ChatColor.of(acolor);
	        } catch (Exception e) {
	          isColor = Boolean.valueOf(false);
	        } 
	        String color = "";
	        String v2 = "";
	        if (!isColor.booleanValue()) {
	          v2 = "#" + v;
	        } else {
	          color = "#" + v.substring(0, 6);
	          v2 = v.substring(6);
	        } 
	        TextComponent tc0 = new TextComponent(v2.replaceAll("&", "§"));
	        if (isColor.booleanValue())
	          tc0.setColor(ChatColor.of(color)); 
	        list.add(tc0);
	      } 
	      count++;
	      b++;
	    } 
	    for (TextComponent tc1 : list)
	      end.addExtra((BaseComponent)tc1); 
	    return end;
	}
	private static String removeRgb(String textold) {
	    if (textold.length() < 12)
		      return textold; 
		    String text = textold.replaceAll("&", "").replaceAll("§", "&");
		    String endText = text;
		    String[] rgb = text.split("&x");
		    byte b;
		    int i;
		    String[] arrayOfString1;
		    for (i = (arrayOfString1 = rgb).length, b = 0; b < i; ) {
		      String value = arrayOfString1[b];
		      if (value.length() >= 12) {
		        String color = value.substring(0, 12);
		        int amount = 0;
		        for (int j = 0; j < color.length(); j++) {
		          if (color.charAt(j) == '&')
		            amount++; 
		        } 
		        if (amount == 6) {
		          String endColor = "&x" + color;
		          String newColor = "#" + color.replaceAll("&", "");
		          endText = endText.replaceAll(endColor, newColor);
		        } 
		      } 
		      b++;
		    } 
		    return endText;
	}
	public String nf(int x) {
		return NumberFormat.getIntegerInstance().format(x);
	}
	public String nfs(String num) {
	int x = 0;
	try {
		x = Integer.parseInt(num);
	} catch(NumberFormatException e){
		
	}
	return NumberFormat.getIntegerInstance().format(x);
}
	private String diferencia(long x) {
		Date date = new Date();
		long ms = date.getTime();
		long s = (ms-x)/1000;
		long d = s/86400;
		long h = (s%86400)/3600;
		long m = ((s%86400)%3600)/60;
		long seconds = s;
		long minutes = seconds/60;
		long hours = minutes/60;
		long days = hours/24;		
		String tiempo = "";
		if(seconds < 60) {		
			tiempo = "§7(hace "+s+" segundos)";
		} else {
			if(hours < 1) {
				if(minutes == 1) tiempo = "§7(hace "+m+" minuto)"; 
				else
				tiempo = "§7(hace "+m+" minutos)";
			} else {
				if(days < 1) {
					if(hours == 1) tiempo = "§7(hace "+h+" hora)";
					else
					tiempo = "§7(hace "+h+" horas)";				
				} else {	
					if(days == 1)  tiempo = "§7(hace "+d+" dia)";
					else
					tiempo = "§7(hace "+d+" dias)";					
				}
			}
		}
		return tiempo;	
    }

}
