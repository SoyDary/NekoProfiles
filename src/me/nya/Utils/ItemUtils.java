package me.nya.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List; 
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.clip.placeholderapi.PlaceholderAPI;
import me.nya.NekoProfiles;


public class ItemUtils {
	
	private static NekoProfiles plugin = NekoProfiles.getInstance();

	
	public static String getTag(ItemStack item, String key) {
		ItemMeta meta = item.getItemMeta();
		String loc_name = meta.getLocalizedName();
		String[] sp = loc_name.split("<;>");
		if(loc_name.equals(""))  return null;
		for(String str : sp) {
			String x = str.split(":")[0];
			String y = str.split(":")[1];
			if(!x.equals(key)) continue;
			return y;
		}
		return null;
	
	}
	public static void setItemPlaceholders(Player p, ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if(meta.hasDisplayName()) {
			meta.setDisplayName(PlaceholderAPI.setPlaceholders(p, meta.getDisplayName()));
		}
		if(meta.hasLore()) {
			meta.setLore(PlaceholderAPI.setPlaceholders(p, meta.getLore()));
		}
		item.setItemMeta(meta);
	}

	public static void setTag(ItemStack item, String key, String value) {
		ItemMeta meta = item.getItemMeta();
		String loc_name = meta.getLocalizedName();
		String[] sp = loc_name.split("<;>");
		if(loc_name.equals("")) {
			meta.setLocalizedName(key+":"+value);
		} else {
			String tag = null;
			for(String str : sp) {
				String x = str.split(":")[0];
				String y = str.split(":")[1];
				if(!x.equals(key)) continue;
				String ff = x+":"+y;
			    tag = loc_name.replaceAll(ff, key+":"+value);
			    meta.setLocalizedName(tag);
				
			}
			if(tag == null) tag = loc_name+"<;>"+key+":"+value;
			meta.setLocalizedName(tag);
			
		}
		item.setItemMeta(meta);
	}

	
	public static ItemStack emptyItem(Material mat) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§b");
		item.setItemMeta(meta);
		return item;
		
	}
	public static ItemStack emptyItem(Material mat, int ammount) {
		ItemStack item = new ItemStack(mat);
		item.setAmount(ammount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§b");
		item.setItemMeta(meta);
		return item;
		
	}
	public static void setName(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(plugin.getUtils().color(name));
		item.setItemMeta(meta);
	}
	public static ItemStack namedItem(Material mat, String name) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(plugin.getUtils().color(name));
		item.setItemMeta(meta);
		return item;	
	}
	
	public static ItemStack getSkullFromTexture(String texture) {	
		if(texture.length() < 65) {
			String originalInput = "{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/"+texture+"\"}}}";
			texture = Base64.getEncoder().encodeToString(originalInput.getBytes());
		}
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);  
		SkullMeta skullMeta = (SkullMeta)item.getItemMeta();
		  GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		  profile.getProperties().put("textures", new Property("textures", texture));
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
	public static void addLoreLine(ItemStack item, String str) {
		ItemMeta im = item.getItemMeta();
		List<String> lines = new ArrayList<String>();
		if(im.hasLore()) lines = im.getLore();
		lines.add(plugin.getUtils().color(str));
		im.setLore(lines);
		item.setItemMeta(im);	
		
	}
	public static void addGlow(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(Enchantment.LURE, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
	}
	public static void setLore(ItemStack item, List<String> lines) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lines);
		item.setItemMeta(meta);
	}
	public static ItemStack itemFromString(String itemID) {
		ItemStack item = null;
		if(itemID.startsWith("HEAD-")) {
			String textureID = itemID.split("HEAD-")[1];
			item = ItemUtils.getSkullFromTexture(textureID);
			return  item;
			
		} else {
			boolean enchanted = itemID.endsWith(";ENCHANTED") ? true : false;
			itemID = itemID.replaceAll(";ENCHANTED", "");		
			if(isItem(itemID)) {
				item = new ItemStack(Material.valueOf(itemID));
				ItemMeta meta = item.getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				item.setItemMeta(meta);
				if(enchanted) ItemUtils.addGlow(item);
				return item;
				
			}		
		}
		return ItemUtils.namedItem(Material.BARRIER, "§c§o*NOMBRE INVÁLIDO*");
	}
	public static ItemStack itemFromString(Player p, String itemID) {
		ItemStack item = null;
		if(itemID.startsWith("HEAD-")) {
			String textureID = itemID.split("HEAD-")[1];
			if(textureID.equals("PLAYER")) return getPlayerSkull(p);
			if(textureID.equals("PLAYER_TEXTURE")) return getHeadFromOF(p.getUniqueId());
			item = ItemUtils.getSkullFromTexture(textureID);
			return  item;
			
		} else {
			boolean enchanted = itemID.endsWith(";ENCHANTED") ? true : false;
			itemID = itemID.replaceAll(";ENCHANTED", "");		
			if(isItem(itemID)) {
				item = new ItemStack(Material.valueOf(itemID));
				ItemMeta meta = item.getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				item.setItemMeta(meta);
				if(enchanted) ItemUtils.addGlow(item);
				return item;			
			}		
		}
		return ItemUtils.namedItem(Material.BARRIER, "§c§o*NOMBRE INVÁLIDO*");
	}
	public static boolean isItem(String str) {
		try {
			Material.valueOf(str);
		} catch(Exception ex) {
			return  false;
		}
		return true;
	}
	public static ItemStack getPlayerSkull(Player p) {
		String texture = plugin.getUtils().getSkinID(p);
		if(texture == null) return getHeadFromOF(p.getUniqueId());
		String originalInput = "{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/"+texture+"\"}}}";
		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);  
		SkullMeta skullMeta = (SkullMeta)item.getItemMeta();
		GameProfile p2 = new GameProfile(p.getUniqueId(), p.getName());
	    p2.getProperties().put("textures", new Property("textures", encodedString));
		try {
		  Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", new Class[] { GameProfile.class });
		  mtd.setAccessible(true);
		  mtd.invoke(skullMeta, new Object[] { p2 });
		} catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException ex) {
		  ex.printStackTrace();
		} 	  
		item.setItemMeta((ItemMeta)skullMeta);
		return item;	
	}
	
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
    	try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);         
            dataOutput.writeInt(items.length);            
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }           
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    

    public static ItemStack getHeadFromOF(UUID uuid) {
    	ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    	SkullMeta meta = (SkullMeta) item.getItemMeta();
    	meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
    	item.setItemMeta(meta);
    	return item;     	
    }


    
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
    	try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
            	items[i] = (ItemStack) dataInput.readObject();
            }
            
            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

}
