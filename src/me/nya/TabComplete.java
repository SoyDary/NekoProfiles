package me.nya;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
public class TabComplete implements TabCompleter {
  static NekoProfiles plugin = NekoProfiles.getInstance();
  
  public List<String> onTabComplete(CommandSender s, Command c, String l, String[] args) {
    if (l.equalsIgnoreCase("profile")) {
        if (args.length == 1) {
	          List<String> commandsList = plugin.getDataManager().getUsers();
	          List<String> preCommands = new ArrayList<>();
	          for (String text : commandsList) {
	            if (text.toLowerCase().startsWith(args[0].toLowerCase()))
	              preCommands.add(text); 
	          } 
	          return preCommands;
	        } 
	  }
    if (l.equalsIgnoreCase("nekoprofiles")|| l.equalsIgnoreCase("np")) {
      if (args.length == 1) {
        List<String> commandsList = new ArrayList<>();
        List<String> preCommands = new ArrayList<>();
        commandsList.add("selectgroup");
        commandsList.add("selectsubgroup");
        commandsList.add("test");
        commandsList.add("extradata");
        for (String text : commandsList) {
          if (text.toLowerCase().startsWith(args[0].toLowerCase()))
            preCommands.add(text); 
        } 
        return preCommands;
      } 
    }
    if (l.equalsIgnoreCase("birthday")|| l.equalsIgnoreCase("bday")) {
        if (args.length == 1) {
          List<String> commandsList = new ArrayList<>();
          List<String> preCommands = new ArrayList<>();
          commandsList.add("set");
          commandsList.add("next");
          for (String text : commandsList) {
            if (text.toLowerCase().startsWith(args[0].toLowerCase()))
              preCommands.add(text); 
          } 
          return preCommands;
        } 
      }


          
    return null;
  }
  
  public void debug(String text) {
    Bukkit.broadcastMessage(text);
  }
}