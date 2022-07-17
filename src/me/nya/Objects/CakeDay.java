package me.nya.Objects;

import java.time.LocalDate;
import java.awt.Color;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;


import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ButtonStyle;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.nya.NekoProfiles;

public class CakeDay {
	private NekoProfiles plugin = NekoProfiles.getPlugin(NekoProfiles.class);
	public int month;
	public int day;
	public String uuid;
	public NekoProfile profile;
	public String monthString;
	public String bday_date = null;
	DiscordSRV discord = (DiscordSRV)Bukkit.getPluginManager().getPlugin("DiscordSRV");	  
	LocalDate date = LocalDate.now();
	
	private int translateMonth(String month) {
		int mes = 0;
		if(month.equals("Enero")) mes = 1; 
		if(month.equals("Febrero")) mes = 2;
		if(month.equals("Marzo")) mes = 3;
		if(month.equals("Abril")) mes = 4;
		if(month.equals("Mayo")) mes = 5;
		if(month.equals("Junio")) mes = 6;
		if(month.equals("Julio")) mes = 7;
		if(month.equals("Agosto")) mes = 8;
		if(month.equals("Septiembre")) mes = 9;
		if(month.equals("Octubre")) mes = 10;
		if(month.equals("Noviembre")) mes = 11;
		if(month.equals("Diciembre")) mes = 12;
		return mes;		
	}
	public CakeDay(String uuid) {
		String date = plugin.getDataManager().getBirthDay(uuid);
		if(date != null) {
			String StringDay = date.split("/")[0];
			String StringMonth = date.split("/")[1];
			Integer day = StringDay.startsWith("0") ? Integer.valueOf(StringDay.replaceAll("0", ""))  : Integer.valueOf(StringDay);
			this.monthString = StringMonth;
			this.month = translateMonth(StringMonth);
			this.uuid = uuid;
			this.day = day;
			this.profile = new NekoProfile(uuid);
			this.bday_date = date;
		}
		
	}
	public boolean hasBeenCelebratedInDiscord() {
		if(plugin.getDataManager().accounts.getString(uuid+".birthday.celebrated_discord") == null) {
			return false;			
		} else {
			return true;
		}
		
	}
	public boolean hasBeenCelebratedInMinecraft() {
		if(plugin.getDataManager().accounts.getString(uuid+".birthday.celebrated_minecraft") == null) {
			return false;			
		} else {
			return true;
		}
		
	}
	
	public void announceInDiscord() {
		sendDiscordEmbed();
		plugin.getDataManager().accounts.set(uuid+".birthday.celebrated_discord", date.getYear());
		plugin.getDataManager().saveAccounts();
		
		
	}
	public void announceInMinecraft() {
		ConsoleCommandSender console = Bukkit.getConsoleSender();
		Bukkit.broadcastMessage(plugin.getUtils().color("&#1affc6☄ &#ffff66¡Hoy es el cumpleaños de &#ff80ff"+profile.getUserName()+" &#ffff66vengan a festajarl"+profile.getGender().getTag("suffix")+"!"));
		for (Player player : Bukkit.getOnlinePlayers()) player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 1);			
		Bukkit.dispatchCommand(console, "minecraft:give @a cake 5");
		plugin.getUtils().spawnFireworks(profile.p.getLocation(), 3);	
		plugin.getDataManager().accounts.set(uuid+".birthday.celebrated_minecraft", date.getYear());
		plugin.getDataManager().saveAccounts();
			
	}
	public String getDateFormatted() {
		return null;
		
	}
	public boolean isToday() {
		boolean isDay = date.getDayOfMonth() == this.day ? true : false;
		boolean isMonth = date.getMonthValue() == this.month ? true : false;
		if(isDay && isMonth) 
			return true;
		else 
			return false;
	}
	
	public void forceCelebrate() {
		Bukkit.broadcastMessage(plugin.getUtils().color("Hoy es el cumpleaños de "+profile.getUserName()));	
	}
	
	private void sendDiscordEmbed() {
		Role cumrole = plugin.getDiscord().getMainGuild().getRoleById("847480623802089513");
		TextChannel channel = (TextChannel) discord.getJda().getGuildChannelById("980239328460562452");
		String user = profile.getUserName();
		long id = profile.getDiscordID();
		if(id != 0) {
			Member member = plugin.getDiscord().getMainGuild().getMemberById(id);
			if(member != null) {
				user = "`"+member.getUser().getAsTag()+"`";
				//26H -> 93600000
				plugin.getDataManager().addTempRole(member, cumrole, 93600000L);		
			}
		
		}
		EmbedBuilder eb = new EmbedBuilder();
		MessageBuilder mb = new MessageBuilder();
		String msg = "<:blush:801510296325324830> **¡Hoy es el cumpleaños de "+user+"! vengan a felicitarl"+profile.getGender().getTag("suffix")+"** <a:kkstar:879993507815784478>";
		eb.setAuthor("☆ Feliz cumpleaños ☆", null, "https://i.imgur.com/DXjAQIU.png");
		eb.setDescription(msg);
		eb.setColor(Color.decode("#FCADFF"));
		mb.setEmbeds(eb.build());
		mb.setContent("<@&980467931945246750> <@"+id+">");
		Message dmsg = mb.build();
		Button bt = Button.of(ButtonStyle.SUCCESS, "NP-CakeDay", "1", Emoji.fromMarkdown("<:cake_uwu:933408920171577354>"));
		DiscordUtil.queueMessage(channel, dmsg, (message) -> {
			message.editMessage("<@&980467931945246750> <@"+id+">").setActionRow(bt).queue((a)-> {
				a.addReaction("⭐").queue();
			});
		}, true);
	}
	
	
	

}
