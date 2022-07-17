package me.nya.Utils;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ColorManager {
	  public TextComponent getText(String text, Player p) {
		    String ttt = text;
		    ttt = removeRgb(ttt);
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
	  
	  public String removeRgb(String textold) {
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


}
