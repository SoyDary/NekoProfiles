package me.nya.Commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import me.nya.Objects.NekoProfile;
import me.nya.NekoProfiles;
import me.nya.Objects.CakeDay;
import me.nya.Objects.MessageProfile;
import net.kokoricraft.nekotags.NekoTags;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;


public class Comandos implements CommandExecutor {
	private NekoProfiles plugin = NekoProfiles.getPlugin(NekoProfiles.class);
	DiscordSRV discord = (DiscordSRV) Bukkit.getPluginManager().getPlugin("DiscordSRV");
	NekoTags nekotags = (NekoTags) Bukkit.getPluginManager().getPlugin("NekoTags");
	JDA jda = discord.getJda();
	public HashMap<String, String> birthDays = new HashMap<String, String>();
	List<String> calendary = Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");
	
	public net.luckperms.api.model.user.User getLPUser(UUID uuid) {
	    UserManager userManager = plugin.getLuckPermsAPI().getUserManager();
	    CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(uuid);
	    return userFuture.join();
	}
	@Override	
	public boolean onCommand(CommandSender s, Command cmd, String l, String[] a) {
		if(l.equalsIgnoreCase("nekoprofiles") || l.equalsIgnoreCase("np")) {
			Player p = null;
			if(s instanceof Player) p = (Player)s;
			if(p != null) {
				if(!p.hasPermission("np.test")) {
					p.sendMessage("Permisos insuficientes!");
					return true;
				}
				if(a.length != 0) {
					if(a[0].equalsIgnoreCase("selectgroup")) {
					}
					if(a[0].equalsIgnoreCase("selectsubgroup")) {
						
					}
					if(a[0].equalsIgnoreCase("test")) {
						Player p2 = Bukkit.getPlayer(a[1]);
						net.luckperms.api.model.user.User user = this.getLPUser(p2.getUniqueId());		
						List<String> p_groups = new ArrayList<String>();
						
						for(Group group :plugin.getLuckPermsAPI().getGroupManager().getLoadedGroups()) {
							InheritanceNode inheritanceNode = InheritanceNode.builder(group).build();
							if(user.data().contains(inheritanceNode, NodeEqualityPredicate.EXACT).asBoolean()) {
								p_groups.add(group.getName());	
							}									
						}
						for(int i = plugin.getDataManager().group_sorter.size(); i >= 0; i--) {
							String gn = plugin.getDataManager().group_sorter.get(i);
							if(p_groups.contains(gn)) {
								p.sendMessage(""+gn+"  ->  "+plugin.getUtils().color(plugin.getDataManager().neko_groups.get(gn).alias)+"     §7(Puesto #"+i+")");
								break;
					
							}
						}
					}

					if(a[0].equalsIgnoreCase("extradata")) {
						String keyData = a[1];
						if(keyData != null) {
							NekoProfile profile = new NekoProfile(p.getUniqueId().toString());
						      StringBuffer sb = new StringBuffer();
						      for(int i = 2; i < a.length; i++) {
						    	  if (i > 2) sb.append(" "+a[i]); else sb.append(a[i]);
						      }
						      String str = sb.toString();
						      if(str.toString().equals("none")) {
						    	  profile.setExtraData(keyData, null);
						    	  p.sendMessage("§8[§e"+keyData+"§8] §dreseteada!");
						      } else {
						    	  if(str.toString().equals("")) {
						    		  p.sendMessage("§8[§e"+keyData+"§8] §7"+profile.getExtraData(keyData));
						    		  
						    	  } else {
							    	  profile.setExtraData(keyData, str);
							    	  p.sendMessage("§8[§e"+keyData+"§8] §7"+str);
						    	  }

						      }
						      
						} else {
							p.sendMessage("Introduce una key");
						}
					}
				}

			}
		}

		if(l.equalsIgnoreCase("birthday") || l.equalsIgnoreCase("bday")) {
			Player p = null;
			if(s instanceof Player) p = (Player)s;
			
			if(p != null) {
				if(a.length == 0) {
					if(plugin.getDataManager().getBirthDay(p.getUniqueId().toString()) != null) {
						String cum = plugin.getDataManager().getBirthDay(p.getUniqueId().toString());
						String cumday = cum.split("/")[0];
						String fd = cumday.startsWith("0") ? cumday.replaceAll("0", "")  : cumday;
						String cummonth = cum.split("/")[1].toLowerCase();
						p.sendMessage(plugin.getUtils().color("&e☄ &#99ffffYa tienes un cumpleaños establecido &f("+fd+" de "+cummonth+")"));	
					} else {
						p.sendMessage(plugin.getUtils().color("&e☄ &7Usa &#cccccc/bday set &7para establecer tu cumpleaños."));
					}
					
				} else {
					if(a[0].equalsIgnoreCase("next")) {
						listBirthDays(p);
										
					}
					if(a[0].equalsIgnoreCase("set")) {
						if(plugin.getDataManager().getBirthDay(p.getUniqueId().toString()) == null) {
							p.spigot().sendMessage(getMonthSelector());	
						} else {
							p.sendMessage(plugin.getUtils().color("&e☄ &#df80ffYa tienes un cumpleaños establecido."));
						}
										
					}
					if(a[0].equalsIgnoreCase("days-menu")) {			
						p.spigot().sendMessage(getDaySelector(a[1]));	
					}
					if(a[0].equalsIgnoreCase("confirm-birthday")) {			
						p.spigot().sendMessage(confirmBirthDay(a[1], a[2]));	
					}
					if(a[0].equalsIgnoreCase("set-birthday")) {	
						if(plugin.getDataManager().getBirthDay(p.getUniqueId().toString()) == null) {
							p.sendMessage(plugin.getUtils().color("&e☄ &#df80ff¡Cumpleaños establecido!"));
							plugin.getDataManager().setBirthDay(p.getUniqueId().toString(), a[1]);				
						} else {
							p.sendMessage(plugin.getUtils().color("&e☄ &#df80ffYa tienes un cumpleaños establecido."));
						}
					}
		
					
				}
			
			}
			
			
		}
		if(l.equalsIgnoreCase("profile")) {
			Player p = null;
			if(s instanceof Player) p = (Player)s;
			
			if(p != null) {
				if(a.length == 0) {
					NekoProfile profile = new NekoProfile(p.getUniqueId().toString());
					profile.updateUserName();
					profile.updateLastPrefix();
					profile.updateSkinID();
					profile.updateGender();
					profile.updatePlaceholders();	
					MessageProfile txt = new MessageProfile(profile);
					TextComponent mensaje = txt.getTextComponent();
					p.spigot().sendMessage(mensaje);
				
				} else {
					if(a.length == 1) {
						NekoProfile otherProfile = null;
						for(String str : plugin.getDataManager().getUsers()) {
							if(str.equalsIgnoreCase(a[0])) {
								otherProfile = new NekoProfile(plugin.getDataManager().users.getString(str));
								break;
							}
						}
						if(otherProfile != null) {
							Player p2 = Bukkit.getPlayer(otherProfile.getUniqueID());
							if(p2 != null) {
								otherProfile.updateUserName();
								otherProfile.updateLastPrefix();
								otherProfile.updateSkinID();
								otherProfile.updateGender();
						        otherProfile.updatePlaceholders();			      
						        TextComponent msg = otherProfile.getProfileMessage();
						        p.spigot().sendMessage(msg);
							} else {
								TextComponent msg = otherProfile.getProfileMessage();
								p.spigot().sendMessage(msg);
							}
						
						} else {
							p.sendMessage(plugin.getUtils().color("&5[&dPF&5] &#ff3333No existe ningún perfil creado con ese nombre"));
						}
	
					} else {
						p.sendMessage(plugin.getUtils().color("&5[&dPF&5] &eUsa &#ffff99/profile <nombre>"));
					}
					
				}
				
				
			} else {
				if(a.length == 0) {
					s.sendMessage("Introduce un nombre");
				} else {
					if(a.length == 1) {
						s.sendMessage("Nombre: "+a[0]);	
					} else {
						s.sendMessage("/profile <nombre>");
					}
			
					
				}
				
			}
			

				
			
		}
		return true;
	}
	public void listBirthDays(Player p) {
		List<CakeDay> todayBirthDays = new ArrayList<CakeDay>();
		HashMap<Integer, List<CakeDay>> bdays = new HashMap<Integer, List<CakeDay>>();
		LocalDate date = LocalDate.now();
		for(String account : plugin.getDataManager().valid_accounts) {
			String bday_date = plugin.getDataManager().accounts.getString(account+".birthday.date");
			if(bday_date != null) {
				
				CakeDay cum = new CakeDay(account);
				if(cum.isToday() && !cum.hasBeenCelebratedInMinecraft()) todayBirthDays.add(cum);
				LocalDate tempdate = LocalDate.of(date.getYear(), cum.month, cum.day);
				List<CakeDay> days = bdays.getOrDefault(tempdate.getDayOfYear(), new ArrayList<CakeDay>());
				days.add(cum);
				bdays.put(tempdate.getDayOfYear(), days);	
			}
		}
		
		List<Integer> numeros = new ArrayList<Integer>(); for(int i : bdays.keySet()) numeros.add(i);	
		Collections.sort(numeros); 
		p.sendMessage("");
		p.sendMessage("");
		p.sendMessage("");
		p.sendMessage("");
		p.sendMessage(plugin.getUtils().color("&e》 &f&l&nSiguientes cumpleaños:&r &e《"));
		p.sendMessage("");
		int counter = 0;
		for(int i : numeros) {
			if(i >= date.getDayOfYear()) {
				if(counter < 15) {
					String personas = "";
					Integer day = 0;
					String monthStrng = "";
					List<String> nombres = new ArrayList<String>();
					List<CakeDay> cumples = bdays.get(i);
					for(CakeDay cake: cumples) {
							nombres.add(cake.profile.getUserName());
							day = cake.day;
							monthStrng = cake.monthString;										
					}
					if(nombres.size() == 1) {
						personas = nombres.get(0);		
					} else {
						StringBuilder sb = new StringBuilder();
						for(String str : nombres) {
							String space = str.equals(nombres.get(nombres.size()-2)) ? " &by " : "&b, ";
							sb.append("&f"+str+space);			
						}
						personas = sb.toString().substring(0, sb.toString().length() - 2);		
					}
					p.sendMessage(plugin.getUtils().color("&#1a8cff[&f"+personas+"&#1a8cff] &b-> &7"+day+" de "+monthStrng));
					counter++;	
				}


			}					
		}
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
	
	public void getBirthDays() {
		List<CakeDay> todayBirthDays = new ArrayList<CakeDay>();
		HashMap<Integer, List<CakeDay>> bdays = new HashMap<Integer, List<CakeDay>>();
		for(String account : plugin.getDataManager().accounts.getConfigurationSection("").getKeys(false)) {
			
			if(plugin.getDataManager().getBirthDay(account) != null) {
				List<CakeDay> days = new ArrayList<CakeDay>();
				CakeDay cum = new CakeDay(account);
				days.add(cum);
				if(cum.isToday() && !cum.hasBeenCelebratedInMinecraft()) todayBirthDays.add(cum);
				LocalDate currentDate = LocalDate.now();
				LocalDate tempdate = LocalDate.of(currentDate.getYear(), cum.month, cum.month);
				bdays.put(tempdate.getDayOfYear(), days);			
			}
		}
	}
	@SuppressWarnings("deprecation")
	public TextComponent getMonthSelector() {
		TextComponent end = new TextComponent("\n\n\n\n\n\n\n\n\n\n\n");
		end.addExtra(new TextComponent(plugin.getUtils().getText("&#ffff00» &e&lSelecciona tu mes de nacimiento &#ffff00«\n\n")));
		int i = 0;
		for(String str : calendary) {
			i++;
			String newLine = calendary.size() > i ? "\n" : "";
			TextComponent element = new TextComponent();
			ComponentBuilder cba = new ComponentBuilder();
			element.addExtra(new TextComponent(plugin.getUtils().getText("   &f↳ &b[&#99ffe6"+str+"&b]"+newLine)));
		    cba.append("§7§o"+str);
			HoverEvent ev1 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cba.create());
			element.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bday days-menu "+str));    
			element.setHoverEvent(ev1);
			end.addExtra(element);
			
		}	
		return end;		
	}
	
	@SuppressWarnings("deprecation")
	public TextComponent confirmBirthDay(String month, String day) {
		TextComponent end = new TextComponent("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		String fd = day.startsWith("0") ? day.replaceAll("0", "")  : day;
		end.addExtra(new TextComponent(plugin.getUtils().getText("&#0073e6&m                                                                 \n")));
		end.addExtra(new TextComponent(plugin.getUtils().getText("              &f&l▸ &#33ffcc&lConfirma tu cumpleaños &f&l◂\n\n")));
		end.addExtra(new TextComponent(plugin.getUtils().getText("&#ffff99¿Tu cumpleaños es el &e"+fd+" de "+month+"&#ffff99? ")));
		
		TextComponent acceptButton = plugin.getUtils().getText("&2[&a✔&2]");
		ComponentBuilder cba = new ComponentBuilder();	
		cba.append(new TextComponent(plugin.getUtils().getText("&aConfirmar cumpleaños\n\n")));
		cba.append(new TextComponent(plugin.getUtils().getText("&7&o(No podrás cambiarlo)")));
		HoverEvent ev1 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cba.create());
		acceptButton.setHoverEvent(ev1);
		acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bday set-birthday "+day+"/"+month));  
		
		end.addExtra(acceptButton);
		end.addExtra("   ");
		
		TextComponent denyButton = plugin.getUtils().getText("&4[&c✖&4]");	
		ComponentBuilder cbd = new ComponentBuilder();
		cbd.append(new TextComponent(plugin.getUtils().getText("           &cCancelar\n\n")));
		cbd.append(new TextComponent(plugin.getUtils().getText("&7&oClic para volver a establecer")));
		
		HoverEvent ev2 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cbd.create());
		denyButton.setHoverEvent(ev2);
		denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bday set"));  
		
		end.addExtra(denyButton);
		end.addExtra(new TextComponent(plugin.getUtils().getText("\n\n&#0073e6&m                                                                 ")));

		return end;		
	}
	@SuppressWarnings("deprecation")
	public TextComponent getDaySelector(String month) {
		int days = 0;
		if(month.equals("Enero")) days = 31;
		if(month.equals("Febrero")) days = 28;
		if(month.equals("Marzo")) days = 31;
		if(month.equals("Abril")) days = 30;
		if(month.equals("Mayo")) days = 31;
		if(month.equals("Junio")) days = 30;
		if(month.equals("Julio")) days = 31;
		if(month.equals("Agosto")) days = 31;
		if(month.equals("Septiembre")) days = 30;
		if(month.equals("Octubre")) days = 31;
		if(month.equals("Noviembre")) days = 30;
		if(month.equals("Diciembre")) days = 31;
		
		TextComponent end = new TextComponent("\n\n\n\n\n\n\n\n\n\n\n\n\n");
		end.addExtra(new TextComponent(plugin.getUtils().getText("&#ffff00» &e&lSelecciona tu dia de nacimiento &#ffff00«\n\n")));
		int x = 0;
		for(int i = 1 ; i <= days; i++) {	
			x++;
			String newLine = x == 5? "\n\n" : ""; if (x == 5) x = 0;
			String number = String.valueOf(i);
			String txt = number.length() == 1 ? "0"+number : number;
			TextComponent element = new TextComponent("   ");
			ComponentBuilder cba = new ComponentBuilder();
			element.addExtra(new TextComponent(plugin.getUtils().getText("&e[&f"+txt+"&e] "+newLine)));
			cba.append("§7"+txt);
			HoverEvent ev1 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, cba.create());
			element.setHoverEvent(ev1);
			element.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bday confirm-birthday "+month+" "+txt));    
			end.addExtra(element);
			
		}

		return end;
		
	}


}

