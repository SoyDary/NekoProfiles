package me.nya.Objects;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import com.hotmail.faviorivarola.ActionsChat.Manager;
import NekosPlugins.NekoTeams.NekoTeams;
import NekosPlugins.NekoTeams.Teams;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import me.clip.placeholderapi.PlaceholderAPI;
import me.nya.NekoProfiles;
import me.nya.Objects.NameLookup.PreviousPlayerNameEntry;
import net.kokoricraft.nekocore.NekoCore;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.chat.TextComponent;


public class NekoProfile {
    private NekoTeams teams = (NekoTeams) Bukkit.getPluginManager().getPlugin("NekoTeams");
    private DiscordSRV discord = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");
    private NekoCore ncore = (NekoCore) Bukkit.getPluginManager().getPlugin("NekoCore");
	private NekoProfiles plugin = NekoProfiles.getPlugin(NekoProfiles.class);
	private String uuid;
	private File profileFile;
	private FileConfiguration config;
	public Player p;
	private OfflinePlayer of;

	
	public NekoProfile(String uuid) {
		this.uuid = uuid;
		this.profileFile = new File(plugin.getDataFolder(), "profiles/"+this.uuid+".yml");
		this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.profileFile);
		this.p = Bukkit.getPlayer(UUID.fromString(uuid));
		this.of = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
		createNewProfile();

		
	}
	public boolean exist() {
		return profileFile.exists() ? true : false;
	}
	
	public long getLastSeenInstant() {
		Date date = new Date(this.getLastSeen());
		Instant instant = date.toInstant();
		return instant.toEpochMilli();
	}
	public UUID getUniqueID() {
		return UUID.fromString(uuid);
	}
	public String getUserName() {
		return config.getString("lastName");	
	
	}
	
	public String getCouple(boolean name) {
		String marry = Manager.getMarry(uuid);
		if(name) {
			return ncore.getCore().getPlayerName(marry);
		} else {
			return Manager.getMarry(uuid);		
		}
	}
	public boolean isBooster() {
		return plugin.getDiscord().getMainGuild().getBoosters().contains(plugin.getDiscord().getMainGuild().getMemberById(this.getDiscordID()));
	}
	public void updateUserName() {
		config.set("lastName", p.getName());
		saveData();	
	}
	public String getOnlineStatus() {
		String online = "§a(Online)";
		String offline = "§c(Offline)";
		String status;
		if(p != null) 
			status = online;
		else
			status = offline;
		return status;
				
	}
	
	private void createNewProfile() {
			if (!profileFile.exists()) {
				if(p != null) {
					try {			
						config.save(profileFile);
						Bukkit.getConsoleSender().sendMessage("[NekoProfiles] Creando nuevo perfil: ["+p.getName()+" | "+uuid+".yml]");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
	
			}		
	}
	
	public void updatePlaceholders() {
		HashMap<String, String> papis = plugin.getPAPIToSave();
		if(p != null && papis != null) {
			for(String key : papis.keySet()) {
				config.set("PlaceholerAPI."+key, PlaceholderAPI.setPlaceholders(p, papis.get(key)));					
			}	
			saveData();	
		}

	}
	public void setExtraData(String key, String data) {
		config.set("ExtraData."+key, data);	
		saveData();	
	}
	
	public String getExtraData(String key) {
		return config.getString("ExtraData."+key);
		
	}
	public GenderProfile getGender() {
		return new GenderProfile(uuid);
		
	}
	public void updateGender() {
		GenderProfile gender = new GenderProfile(uuid);
		config.set("gender", gender.getName());
		saveData();		
		
	}
	public User getLPUser() {
	    UserManager userManager = plugin.getLuckPermsAPI().getUserManager();
	    CompletableFuture<User> userFuture = userManager.loadUser(UUID.fromString(uuid));
	    return userFuture.join();
	}
	public String getDiscordTag() {
		if(!discord.getAccountLinkManager().getLinkedAccounts().containsValue(UUID.fromString(uuid)))return "";
		String discordID = discord.getAccountLinkManager().getDiscordIdBypassCache(UUID.fromString(uuid));
		if(discordID != null) {
			long id = Long.parseLong(discordID);
			
			return discord.getJda().retrieveUserById(id).complete().getAsTag();
					
		} else {
			return "";
		}
		
	}
	public long getDiscordID() {
		String discordID = discord.getAccountLinkManager().getDiscordIdBypassCache(UUID.fromString(uuid));
		if(discordID != null) {
			return Long.parseLong(discordID);					
		} else {
			return 0;
		}
		
	}
	public List<String> getUserGroups(){
		List<String> group_order = plugin.getConfig().getStringList("Groups.group_sorting");
		List<String> groups = new ArrayList<String>();
		List<String> ordenados = new ArrayList<String>();
		Map<Integer, String> sorter = new HashMap<Integer, String>();
		User user =  getLPUser();
		groups.add(user.getPrimaryGroup());
		
		for(Group group : plugin.getLuckPermsAPI().getGroupManager().getLoadedGroups()){
			InheritanceNode inheritanceNode = InheritanceNode.builder(group).build();
			if(user.data().contains(inheritanceNode, NodeEqualityPredicate.EXACT).asBoolean()) {
				groups.add(group.getName());				
			}
				
		}

		for(String group : groups) {
			int i = 0;
			for(String str : group_order) {
				i++;
				if(str.equals(group)) {
					sorter.put(i, group);
				}							
			}						
		}
		for(int i = 0; i <= group_order.size(); i++) {
			if(sorter.containsKey(i)) {
				ordenados.add(sorter.get(i));
			}			
		}	
		return ordenados;	
	}
	public void getGroups() {
		User user = plugin.getLuckPermsAPI().getUserManager().getUser(UUID.fromString(uuid));
		user.getPrimaryGroup();
		for(Group gup : user.getInheritedGroups(QueryOptions.defaultContextualOptions())) {
			gup.toString();			
		}
		user.getInheritedGroups(QueryOptions.defaultContextualOptions());	
	}
	
	public void updateSkinID() {
		try {
			String id = getSkinIDD();	
			config.set("skinID", id);
			saveData();		
		} catch(Exception  e) { }
		
	
	}
	public void updateLastPrefix() {
		String prefix = PlaceholderAPI.setPlaceholders(p, "%nekoprofiles_group_prefix%");
		//User user = plugin.getLuckPermsAPI().getUserManager().getUser(p.getUniqueId());
		//CachedDataManager cachedData = user.getCachedData();
		//String prefix = cachedData.getMetaData().getPrefix();
		//if(prefix == null) prefix = "";
		config.set("last_pefix", prefix.replaceAll("§", "&"));	
		saveData();	
	}
	
	public String getLastPrefix() {
		return config.getString("last_pefix");
	}
	public String getSkinID() {
		return config.getString("skinID");
			
	}
	public boolean isPremium(){
		  boolean bl = false;
		  try {
			  HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.mojang.com/user/profiles/"+uuid.replaceAll("-", "")+"/names").openConnection();
			  if(connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				  bl = true;		  
			  }
		  } catch (MalformedURLException  e) {		 
		  } catch (IOException e1) {	  
		  }
		  return bl;	
		
	}

	
	public String getPAPIvalue(String key) {
		String value = config.getString("PlaceholerAPI."+key);
		if(value != null) {
			return config.getString("PlaceholerAPI."+key);
		} else {
			return "";
		}
		
		
	}
	
	public int getStatistic(Statistic stat) {

		int x = of.getStatistic(stat);
		return x;
		
	}
	public void updateProfile() {
			
	}
	public String getFirstJoin() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		Date date = new Date(of.getFirstPlayed());
		return formatter.format(date);	
		
	}
	
	public void updateLastSeen() {
		Date date = new Date();
		config.set("lastSeen", date.getTime());
		saveData();
		
	}
	public long getLastSeen() {
		if(p != null) {
			return config.getLong("lastSeen");	
		} else {
			return of.getLastPlayed();
		}
	}
	public boolean isOnline() {
		boolean bl = false;
		if(p != null) {
			bl = true;
		}
		return bl;
	}
	public List<String> getNameHistory(){
		List<String> history = new ArrayList<String>();
	    PreviousPlayerNameEntry[] previousNames = NameLookup.getPlayerPreviousNames(uuid);
        for(PreviousPlayerNameEntry entry : previousNames){
        	history.add(entry.getPlayerName()+"-"+entry.getChangeTime());
        }
        for (int i = 0, j = history.size() - 1; i < j; i++) {
        	history.add(i, history.remove(j));
        }
        return history;
	}
	
	public String getPAPIStat(Statistic stat) {
		return PlaceholderAPI.setPlaceholders(of, "%statistic_"+stat.name().toLowerCase()+"%");
		
	}
	
	public TextComponent getProfileMessage() {
		MessageProfile txt = new MessageProfile(this);
		return txt.getTextComponent();
				
	}
	public Message getDiscordProfileMessage() {
		MessageProfile txt = new MessageProfile(this);
		return txt.getDiscordMessage();
				
	}
	public String getLastPlayed() {	
		Date date = new Date();
		long x = config.getLong("lastSeen");
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
			tiempo = "(desde hace "+s+" segundos)";
		} else {
			if(hours < 1) {
				if(minutes == 1) tiempo = "(desde hace "+m+" minuto)"; 
				else
				tiempo = "(desde hace "+m+" minutos)";
			} else {
				if(days < 1) {
					if(hours == 1) tiempo = "(desde hace "+h+" hora)";
					else
					tiempo = "(desde hace "+h+" horas)";				
				} else {	
					if(days == 1)  tiempo = "(desde hace "+d+" dia)";
					else
					tiempo = "(desde hace "+d+" dias)";					
				}
			}
		}
		return tiempo;	
	}
	public String getLastSeenFormated() {
		long x = getLastSeen();
        Date d2 = new Date(); 
		Date d1 = new Date(x);
		long time = d2.getTime() - d1.getTime();
		long s = (time/1000)%60;	               String sf = "";
		long m = (time/(1000*60))%60;	           String mf = ""; 
		long h = (time/(1000*60 *60))%24;	       String hf = ""; 
		long y = (time/(1000l*60*60*24*365));      String yf = "";
		long d = (time/(1000*60*60*24))%365;       String df = "";
		if(s != 0) { if(s == 1) sf = s+"s"; else sf = s+"s"; }
		if(m != 0) { if(m == 1) mf = m+"m "; else mf = m+"m "; }
		if(h != 0) { if(h == 1) hf = h+"h "; else hf = h+"h "; }
		if(y != 0) { if(y == 1) yf = y+" año "; else yf = y+" años "; }
		if(d != 0) { if(d == 1) df = d+"d "; else df = d+"d "; }
		return "(hace "+yf+df+hf+mf+sf+")";
		
	}
	
	public Teams getTeam() {
		return teams.getTeamManager().getPlayerTeam(uuid);
	}
	public GameProfile getGameProfile() {
		try {
			Class<?> strClass = (Class<?>) Class.forName("org.bukkit.craftbukkit."+plugin.server_version+".entity.CraftPlayer");
			GameProfile profile = (GameProfile) strClass.cast(p).getClass().getMethod("getProfile").invoke(strClass.cast(p));
			return profile;			
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			return null;
		}
		
	}


	  
	private String getSkinIDD() {
		GameProfile profile = getGameProfile();
		if(profile == null) return null;
		Property property = profile.getProperties().get("textures").iterator().next();
		String b64texture = property.getValue();
		byte[] valueDecoded = Base64.getDecoder().decode(b64texture);				
		String id = new String(valueDecoded).split("url")[1].split("\"")[2].split("texture/")[1];
		return id;	
	}
	
	private void saveData() {			
		try {		
			this.config.save(this.profileFile);			
			} catch (IOException e) {
			  e.printStackTrace();
			}
	}
	public ItemStack getProfileHead() {	
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);  
		SkullMeta skullMeta = (SkullMeta)item.getItemMeta();
		  GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		  profile.getProperties().put("textures", new Property("textures", getEncodedSkinID()));
		  try {
		    Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", new Class[] { GameProfile.class });
		    mtd.setAccessible(true);
		    mtd.invoke(skullMeta, new Object[] { profile });
		  } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
		    ex.printStackTrace();
		  } 	  
		  item.setItemMeta((ItemMeta)skullMeta);
		  return item;
		
	}
	public String getEncodedSkinID() {
		String originalInput = "{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/"+getSkinIDD()+"\"}}}";
		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		return encodedString;
		
	}


}
