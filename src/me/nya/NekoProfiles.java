package me.nya;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.Command;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import me.nya.API.PAPI2;
import me.nya.API.PlaceholderAPI;
import me.nya.Commands.Comandos;
import me.nya.Commands.NekoGroups;
import me.nya.Listeners.Eventos;
import me.nya.Listeners.JDAListener;
import me.nya.Managers.DataManager;
import me.nya.Managers.GroupsManager;
import me.nya.Objects.CakeDay;
import me.nya.Utils.Utils;
import net.luckperms.api.LuckPerms;

public class NekoProfiles extends JavaPlugin {
	DataManager datamanager;
	GroupsManager groupsmanager;
	Utils utils;
	LuckPerms luckperms;
	DiscordHook discordhook;
    DiscordSRV discord = (DiscordSRV)Bukkit.getPluginManager().getPlugin("DiscordSRV");	 
    public String server_version;
    public Boolean is_nekoteams = false;
    public Boolean is_nekotags = false;
    public Boolean is_actionschat = false;
    public Boolean is_nekocore = false;
	JDA jda = discord.getJda();
	int times = 1;
	int t = 0;
	
	public void onEnable() {
		if(DiscordSRV.isReady) {
			enablePlugin();	
		} else {
			tryEnable();
		}		
	}
	
	public void enablePlugin() {
		this.utils = new Utils(this);
		this.datamanager = new DataManager(this);
		this.groupsmanager = new GroupsManager(this);
		this.luckperms = (LuckPerms)getServer().getServicesManager().load(LuckPerms.class);
		Bukkit.getConsoleSender().sendMessage("[NekoProfiles] Activado uwu");
		Bukkit.getPluginManager().registerEvents(new Eventos(), (Plugin)this);
		registerCommands();
		loadConfig();
		this.discordhook = new DiscordHook(this);
		this.discordhook.loadDiscordHook();
		new PlaceholderAPI(this).register();
		new PAPI2(this).register();
		registerDiscordJDA();
		datamanager.onStart();
		todayBirthDays();
		checkRoles();
		loadDependencies();	
		this.server_version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
	}
	
	public void tryEnable() {
	    t = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		public void run() {	
			if(!DiscordSRV.isReady) {
				times++;
				if(times <= 6) {
				} else {
					cancelEnable();
					Bukkit.getPluginManager().disablePlugin((Plugin) this);
				}			
			} else {
				enablePlugin();	
				cancelEnable();			
			}		
		}		
		}, 60L, 60L);
	}
	
	
	public void cancelEnable() {
		Bukkit.getScheduler().cancelTask(t);
	}
	
	public void loadDependencies() {
		if(getServer().getPluginManager().getPlugin("NekoTeams") != null) is_nekoteams = true;
		if(getServer().getPluginManager().getPlugin("NekoTags") != null) is_nekotags = true;
		if(getServer().getPluginManager().getPlugin("ActionsChat") != null) is_actionschat = true;
		if(getServer().getPluginManager().getPlugin("NekoCore") != null) is_nekocore = true;
	}
	public void todayBirthDays() {	
		for(String account : datamanager.valid_accounts) {
			String bday_date = getDataManager().accounts.getString(account+".birthday.date");
			if(bday_date != null) {
				CakeDay cumple = new CakeDay(account);
				if(cumple.isToday() && !cumple.hasBeenCelebratedInDiscord()) {
					cumple.announceInDiscord();
				}
			}
		}
	}
	  public void registerDiscordJDA() {
		  
		    List<Object> listeners = discord.getJda().getRegisteredListeners();
		    RestAction<List<Command>> comandos = discord.getJda().getGuildById(discord.getMainGuild().getIdLong()).retrieveCommands();
		    String status = "[NekoProfiles - JDA] Registrado un nuevo listener..";
		    if (listeners != null) {
		      for (Object ob : listeners) {
		    	  if(ob.getClass().getName().contains("me.nya.Listeners.JDAListener")) {	
		    		  
		    		  status = "[NekoProfiles - JDA] Actualizado el listener de discord";
		    		  discord.getJda().removeEventListener(ob);
		    	  }   
		      }
		    }
		    for (Command cm : comandos.complete()) {
		    	if(cm.getName().equals("profile")) {
		    		 cm.delete().complete();	    		 
		    	} 
		    	if(cm.getName().equals("tim")) {
		    		 cm.delete().complete();	    		 
		    	}   
		    } 
		    discord.getJda().updateCommands();
		    
		    discord.getJda().addEventListener(new JDAListener(this));
		    discord.getJda().upsertCommand("test", "test command");
		    
		    Bukkit.getConsoleSender().sendMessage(status);
		  
		    
		    discord.getJda().upsertCommand("test", "test command");
		    CommandData cmdata = new CommandData("profile", "📋 Mira el perfil público tuyo o de alguien mas.");
		    cmdata.addOption(OptionType.STRING, "nick", "Escribe el nick el jugador dentro del servidor", false);
		    cmdata.addOption(OptionType.USER, "discord", "Menciona a alguien de discord", false);
		    discord.getJda().upsertCommand(cmdata).queue();
		    
		  }
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("[NekoProfiles] Desactivado unu");
		this.discordhook.unloadDiscordHook();
		}
	public List<String> getGenderList(){
		List<String> genders = new ArrayList<String>();
		for(String str : getConfig().getConfigurationSection("Genders").getKeys(false)) {
			genders.add(str);
		}
		return genders;
	}
	public HashMap<String, String>  getPAPIToSave(){
		HashMap<String, String> papis = new HashMap<String, String>();
		for(String str : getConfig().getConfigurationSection("FollowPlaceholders").getKeys(false)) {
			papis.put(str, getConfig().getString("FollowPlaceholders."+str));
		}
		return papis;
	}
	public void registerCommands() {
		getCommand("profile").setExecutor(new Comandos());
		getCommand("profile").setTabCompleter(new TabComplete());	
		getCommand("nekoprofiles").setExecutor(new Comandos());	
		getCommand("nekoprofiles").setTabCompleter(new TabComplete());	
		getCommand("birthday").setExecutor(new Comandos());	
		getCommand("nekogroups").setExecutor(new NekoGroups());	
		getCommand("birthday").setTabCompleter(new TabComplete());	
	}

	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	public DataManager getDataManager() {		
	    return this.datamanager;	    
	}
	public GroupsManager getGroupsManager() {		
	    return this.groupsmanager;	    
	}
	public Utils getUtils() {		
	    return this.utils;    
	}
	public static NekoProfiles getInstance() {
		return (NekoProfiles)JavaPlugin.getPlugin(NekoProfiles.class);
	}
	public LuckPerms getLuckPermsAPI() {
		return this.luckperms;
	}
	public DiscordSRV getDiscord() {
		return this.discord;
	}
	
	
	public void checkRoles() {
		for(String nya : getDataManager().data.getConfigurationSection("").getKeys(false)) {
			for(String srole : getDataManager().data.getConfigurationSection(nya).getKeys(false)) {
				Long date = new Date().getTime();
				Long expire = getDataManager().data.getLong(nya+"."+srole+".expire");
				Long rdate = getDataManager().data.getLong(nya+"."+srole+".date");
				if(date >= rdate+expire) {
					getDataManager().data.set(nya+"."+srole, null);
					getDataManager().saveData();
					Member mem = discord.getMainGuild().getMemberById(nya);
					if(mem != null) {
						Role role = discord.getMainGuild().getRoleById(srole);
						if(role != null) {
							if(mem.getRoles().contains(role)) {
								discord.getMainGuild().removeRoleFromMember(mem, role).queue();
							}
						}
					}
				}
			}
		}	
	}
}