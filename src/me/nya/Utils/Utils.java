package me.nya.Utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.nya.NekoProfiles;

public class Utils {
  NekoProfiles plugin;
  
  Boolean hasPlaceholderApi = Boolean.valueOf(false);
  
  private final Pattern pattern;
  

  public Utils(NekoProfiles plugin) {
    this.pattern = Pattern.compile("(?<!\\\\)(#[a-fA-F0-9]{6})");
    this.plugin = plugin;
    Plugin pha = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI");
    if (pha != null)
      this.hasPlaceholderApi = Boolean.valueOf(pha.isEnabled()); 
  }
  public String color(String text) {
	    String end = "";
	    if (text == null || text == "")
	      return ""; 
	    String text2 = parseColor(text);
	    String[] words = text2.split(Pattern.quote("&#"));
	    if (words.length != 0) {
	      int count = 0;
	      byte b;
	      int i;
	      String[] arrayOfString;
	      for (i = (arrayOfString = words).length, b = 0; b < i; ) {
	        String t = arrayOfString[b];
	        String more = "";
	        if (count != 0)
	          more = "#"; 
	        String t2 = t;
	        t2 = normalColor(t2);
	        t2 = HexColor(String.valueOf(more) + t2);
	        end = String.valueOf(end) + t2;
	        count++;
	        b++;
	      } 
	      return end;
	    } 
	    return text;
	  }
  public boolean isPremium(String uuid){
	  try {
		  HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.mojang.com/user/profiles/"+uuid.replaceAll("-", "")+"/names").openConnection();
		  if(connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
			  return true;
		  }
	  } catch (MalformedURLException  e) {		 
	  } catch (IOException e1) {	  
	  }
	  return false;  
  }
	public User getLPUser(UUID uuid) {
	    UserManager userManager = plugin.getLuckPermsAPI().getUserManager();
	    CompletableFuture<User> userFuture = userManager.loadUser(uuid);
	    return userFuture.join();
	}
  private String parseColor(String text) {
	    if ((((text.length() == 0) ? 1 : 0) | ((text == null) ? 1 : 0)) != 0 || text.length() < 7)
	      return text; 
	    String tedit = text;
	    String text2 = text;
	    for (int i = text2.length() - 1; i > 0; i--) {
	      String c = (new StringBuilder(String.valueOf(text2.charAt(i)))).toString();
	      if (c.contains("#") && i - 1 < 0 && text2.length() >= 7) {
	        String color = String.valueOf(c) + text2.charAt(i + 1) + text2.charAt(i + 2) + text2.charAt(i + 3) + text2.charAt(i + 4) + text2.charAt(i + 5) + text2.charAt(i + 6);
	        if (isColor(color))
	          tedit = addChar(text2, "&", i); 
	      } else if (c.contains("#") && i - 1 > 0 && i + 6 <= text2.length() - 1) {
	        String color = String.valueOf(c) + text2.charAt(i + 1) + text2.charAt(i + 2) + text2.charAt(i + 3) + text2.charAt(i + 4) + text2.charAt(i + 5) + text2.charAt(i + 6);
	        if (isColor(color) && !(new StringBuilder(String.valueOf(text2.charAt(i - 1)))).toString().contains("&"))
	          tedit = addChar(text2, "&", i); 
	      } 
	    } 
	    return tedit;
	  }
  private String addChar(String str, String ch, int position) {
	    return String.valueOf(str.substring(0, position)) + ch + str.substring(position);
	  }
  private boolean isColor(String text) {
	    String text2 = text;
	    if (text.startsWith("&"))
	      text2 = text.replaceFirst("&", ""); 
	    try {
	      ChatColor.of(text2);
	      return true;
	    } catch (Exception ex) {
	      return false;
	    }     
	  }

  private String HexColor(String message) {
	    Matcher matcher = this.pattern.matcher(message);
	    while (matcher.find()) {
	      String color = message.substring(matcher.start(), matcher.end());
	      Boolean isColor = Boolean.valueOf(false);
	      try {
	        ChatColor.of(color);
	        isColor = Boolean.valueOf(true);
	      } catch (Exception exception) {}
	      if (isColor.booleanValue())
	        message = message.replace(color, ""+ChatColor.of(color)); 
	    } 
	    return message;
	  }
	  
  private String normalColor(String message) {
	    message = ChatColor.translateAlternateColorCodes('&', message);
	    return message;
	  }
  public void spawnFireworks(Location location, int amount){
      Location loc = location;
      Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
      FireworkMeta fwm = fw.getFireworkMeta();
     
      fwm.setPower(2);
      fwm.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).flicker(true).build());
     
      fw.setFireworkMeta(fwm);
      fw.detonate();
     
      for(int i = 0;i<amount; i++){
          Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
          fw2.setFireworkMeta(fwm);
      }
  }
  public TextComponent advColor(String text) {
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
  private String removeRgb(String textold) {
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
	  
  public String dateDiff(long x) {
	        Date d2 = new Date(); 
			Date d1 = new Date(x);
			long time = d2.getTime() - d1.getTime();
			long s = (time/1000)%60;	  
			long m = (time/(1000*60))%60;	  
			long h = (time/(1000*60 *60))%24;	  
			long y = (time/(1000l*60*60*24*365));
			long d = (time/(1000*60*60*24))%365;
			
			return y+ " años, "+ d+ " días, "+ h+ " horas, "+ m+ " minutos, "+ s+ " segundos";
  
		
	    }
  public String dateDiff2(long x) {
	        Date d2 = new Date(); 
			Date d1 = new Date(x);
			long time = d1.getTime() - d2.getTime();
			long s = (time/1000)%60;	  
			long m = (time/(1000*60))%60;	  
			long h = (time/(1000*60 *60))%24;	  
			long y = (time/(1000l*60*60*24*365));
			long d = (time/(1000*60*60*24))%365;
			
			return y+ " años, "+ d+ " días, "+ h+ " horas, "+ m+ " minutos, "+ s+ " segundos";

		
	    }
  public TextComponent getText(String text) {
	    String ttt = text;
	    ttt = removeRGB(ttt);
	    String[] rgb = ChatColor.translateAlternateColorCodes('&', ttt).split(Pattern.quote("&#"));
	    ArrayList<TextComponent> list = new ArrayList<>();
	    TextComponent end = new TextComponent("");
	    int count = 0;
	    byte b;
	    int i;
	    String[] arrayOfString1;
	    for (i = (arrayOfString1 = rgb).length, b = 0; b < i; ) {
	      String v = arrayOfString1[b];
	      if (count == 0) {
	        String v2 = v;
	        TextComponent tc0 = new TextComponent(v2);
	        list.add(tc0);
	      } else {
	        String color = "#" + v.substring(0, 6);
	        String v2 = v.substring(6);
	        //v2 = PlaceholderAPI.setPlaceholders(p, v2.replaceAll("%dcu%", "%discordsrv_user_tag%").replaceAll("%dcu2%", "%javascript_no_discord%"));
	        TextComponent tc0 = new TextComponent(v2);
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

public String removeRGB(String textold) {
	    if (textold.length() < 12)
	      return textold; 
	    String text = textold.replaceAll("§", "&");
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
	          String newColor = "&#" + color.replaceAll("&", "");
	          endText = endText.replaceAll(endColor, newColor);
	        } 
	      } 
	      b++;
	    } 
	    return endText;
	  }
public GameProfile getGameProfile(Player p) {
		try {
			Class<?> strClass = (Class<?>) Class.forName("org.bukkit.craftbukkit."+plugin.server_version+".entity.CraftPlayer");
			GameProfile profile = (GameProfile) strClass.cast(p).getClass().getMethod("getProfile").invoke(strClass.cast(p));
			return profile;			
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			return null;
		}
}
public String getSkinID(Player p) {
		GameProfile profile = getGameProfile(p);
		if(profile == null) return null;
		Property property = profile.getProperties().get("textures").iterator().next();
		String b64texture = property.getValue();
		byte[] valueDecoded = Base64.getDecoder().decode(b64texture);				
		String id = new String(valueDecoded).split("url")[1].split("\"")[2].split("texture/")[1];
		return id;	
	}

}
