package me.nya.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class ComponentUtil {
public static List<Partecita> getTextComponents(String mensajito, Player player){
	List<Partecita> components = new ArrayList<>();
	String oldColor = "";
	for(ColorSection section : breakText(mensajito)) {
		TextComponent partComponent = new TextComponent();
		String partText = PlaceholderAPI.setPlaceholders(player, section.getText());
		partText = removeRgb(partText);
		partText = parseColor(partText);
		partText = partText.replaceAll("<PorcentajeCodigoRemplazado>", "%");
		partText = partText.replaceAll("<priceCode>", Matcher.quoteReplacement("$"));
		partText = ChatColor.translateAlternateColorCodes('&', partText);
		for(ColorSection endSection : getSections(partText)) {
			TextComponent mComponent = new TextComponent(endSection.getText());
			
			if(endSection.getColor() != null && endSection.getColor() != "") {
				mComponent.setColor(ChatColor.of(endSection.getColor()));
				oldColor = endSection.getColor();
			}else if(oldColor != ""){
				if(oldColor.contains("#")) {
					mComponent.setColor(ChatColor.of(oldColor));
				}else {
					mComponent.setColor(ChatColor.getByChar(oldColor.charAt(1)));
				}
			}
			partComponent.addExtra(mComponent);
			String noldColor = getLastColor(endSection.getColor()+endSection.getText());
			if(noldColor != "") {
				oldColor = noldColor;
			}
		}
		Partecita partecita = new Partecita(partComponent, section.getText());
		components.add(partecita);
	}
	return components;
}

public static String getLastColor(String text) {
	String lastColor = "";
	if(text == null || text.equals("") || text.length() <= 1) {
		return lastColor;
	}
	for(int i = 0; i < text.length(); i++) {
		String c = text.charAt(i)+"";
		if(c.contains("&")) {
			if(i+7 < text.length()-1 && (text.charAt(i+1)+"").contains("#")) {
				String color = ""+text.charAt(i+1)+text.charAt(i+2)+text.charAt(i+3)+text.charAt(i+4)+text.charAt(i+5)+text.charAt(i+6)+text.charAt(i+7);
				if(isColor(color)) {
					lastColor = color;
				}
			}else {
				if(i+1 <= text.length()-1) {
					String next = text.charAt(i+1)+"";
					if(normalsColor().contains(next)) {
						lastColor = "&"+next;
					}
				}
			}
		}else if(c.contains("ยง")) {
			if(i+1 <= text.length()-1) {
				String next = text.charAt(i+1)+"";
				if(normalsColor().contains(next)) {
					lastColor = "&"+next;
				}
			}
		}
	}
	
	if(lastColor.contains("&") || lastColor.contains("ยง")) {
		lastColor = org.bukkit.ChatColor.getLastColors(text);
	}
	if(isColor(text)) {
		lastColor = text.replaceFirst("&", "");
	}
	
	return lastColor;
}

public static ArrayList<ColorSection> getSections(String text){
	ArrayList<ColorSection> sections  = new ArrayList<ColorSection>();
	String[] words = text.split(Pattern.quote("&#"));
	if(words.length != 0) {
		int count = 0;
		for(String t : words) {
			String more = "";
			if(count != 0) {
				more = "#";
			}
			sections.add(buildSection(more+t));
			count++;
		}
	}else {
		sections.add(new ColorSection(text));
	}
	
	return sections;
}

private static final Pattern pattern = Pattern.compile("(?<!\\\\)(#[a-fA-F0-9]{6})");

public static ColorSection buildSection(String message) {
	ColorSection section = new ColorSection();
	section.setText(message);
	 Matcher matcher = pattern.matcher(message);
	    while (matcher.find()) {
	      String color = message.substring(matcher.start(), matcher.end());
	      if(isColor(color)) {
	    	  section.setText(message.replaceFirst(color, ""));
	    	  section.setColor(color);
	      }
	    }    
	    return section;
}

private static ArrayList<String> normalColors = new ArrayList<>();
public static ArrayList<String>  normalsColor(){
	if(normalColors.isEmpty()) {
		loadNormalColors();
	}
	return normalColors;
}

private static void loadNormalColors() {
	normalColors.add("a");
	normalColors.add("b");
	normalColors.add("c");
	normalColors.add("d");
	normalColors.add("e");
	normalColors.add("f");
	normalColors.add("1");
	normalColors.add("2");
	normalColors.add("3");
	normalColors.add("4");
	normalColors.add("5");
	normalColors.add("6");
	normalColors.add("7");
	normalColors.add("8");
	normalColors.add("9");
	normalColors.add("0");
	normalColors.add("r");
}

public static String parseColor(String text) {
	if(text == null || text.length() < 7) {
		return text;
	}
	List<String> indexlist = new ArrayList<String>();
	String endText = text;
	for(int i = 0; i < text.length()-1; i++) {
		String c = text.charAt(i)+"";
		String later = null;
		if(i-1 >= 0) {
			later = text.charAt(i-1)+"";
		}
		if(c.equals("#")) {
			
			if(i+6 <= text.length()-1) {
				if(later == null) {
					if(isColor(buildText(text, i))) {
						indexlist.add(i+"");
					}
				}else {
					if(!later.equals("&")) {
						if(isColor(buildText(text, i))) {
							indexlist.add(i+"");
						}
					}
				}
				
			}
		}
	}
	for(int i = 0; i < indexlist.size(); i++) {
		int index = Integer.parseInt(indexlist.get(i));
		endText = addChar(endText, "&", index+i);
	}
	return endText;
}

public static String buildText(String text, int i) {
	return ""+text.charAt(i)+text.charAt(i+1)+text.charAt(i+2)+text.charAt(i+3)+text.charAt(i+4)+text.charAt(i+5)+text.charAt(i+6)+"";
}

public static String addChar(String str, String ch, int position) {
    return str.substring(0, position) + ch + str.substring(position);
}

public static boolean isColor(String text) {
	String text2 = text;
	if(text.startsWith("&")) {
		text2 = text.replaceFirst("&", "");
	}
    try {
  	  ChatColor.of(text2);
  	  return true;
    }catch(Exception ex) {
  	  return false;
    }
}

public static  String removeRgb(String textold) {
	if(textold.length() < 12) {
		return textold;
	}
	String text = textold.replaceAll("ยง", "&");
	String endText = text;
	
	String[] rgb = text.split("&x");
	for(String value : rgb) {
		if(value.length() < 12) {
			continue;
		}
		String color = value.substring(0, 12);
		int amount = 0;
		for(int i = 0; i < color.length(); i++) {
			if(color.charAt(i) == '&') {
				amount++;
			}
		}
		if(amount == 6) {
			String endColor = "&x"+color;
			String newColor = "&#"+color.replaceAll("&", "");
			endText = endText.replaceAll(endColor, newColor);
		}
	}
	return endText;
}

public static ArrayList<ColorSection> breakText(String text){
	ArrayList<ColorSection> vars = new ArrayList<ColorSection>();
	String cacheText = "";
	String cacheVar = "";
	Boolean runningVar = false;
	for(int i = 0; i < text.length(); i++) {
		String c = text.charAt(i)+"";
		if(c.contains("%") && runningVar == false) {
			runningVar = true;
			if(cacheText != null && cacheText != "") {
				ColorSection section = new ColorSection(cacheText);
					vars.add(section);
				cacheText = "";
			}
			continue;
		}
		if(runningVar) {
			cacheVar += text.charAt(i);
			if(text.charAt(i) == '%') {
				runningVar = false;
				String vv = "%"+cacheVar;
				ColorSection section = new ColorSection(vv);
					section.setVar();
					vars.add(section);
				cacheVar = "";
			}
		}else {
			cacheText += text.charAt(i);
			if(i == text.length()-1) {
				ColorSection section = new ColorSection(cacheText);
					vars.add(section);
			}
		}
	}
	
	return vars;
}

}

class ColorSection{
	String text;
	String color;
	Boolean isVar = false;
public ColorSection(String text) {
	this.text = text;
}

public ColorSection(String text, String color) {
	this.text = text;
	this.color = color;
}


public ColorSection() {
	//ignorar esto owo
}


public boolean isVar() {
	return this.isVar;
}


public String getText() {
	return this.text;
}

public String getColor() {
	if(this.color != null && this.color.length() == 8) {
		this.color = this.color.replaceFirst("&", "");
	}
	return this.color;
}

public boolean isColor(String text) {
	String text2 = text;
	if(text.startsWith("&")) {
		text2 = text.replaceFirst("&", "");
	}
    try {
    	net.md_5.bungee.api.ChatColor.of(text2);
  	  return true;
    }catch(Exception ex) {
  	  return false;
    }
}

public boolean isColor() {
	if(this.text == null) {
		return false;
	}
	String text2 = this.text;
	if(text.startsWith("&")) {
		text2 = text.replaceFirst("&", "");
	}
    try {
    	net.md_5.bungee.api.ChatColor.of(text2);
  	  return true;
    }catch(Exception ex) {
  	  return false;
    }
}


public String getLastNormalColor() {
	String oldColor = "";
	if(this.text == null|| this.text == "" ) {
		return oldColor;
	}
	
	String text = ChatColor.translateAlternateColorCodes('&', this.text);
	oldColor = org.bukkit.ChatColor.getLastColors(text);
	return oldColor;
}

public void setText(String text) {
	this.text = text;
}

public void setColor(String color) {
	this.color = color;
}

public void setVar() {
	this.isVar = true;
}
}
