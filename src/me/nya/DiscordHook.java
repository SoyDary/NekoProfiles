package me.nya;

import github.scarsz.discordsrv.DiscordSRV;
import me.nya.Listeners.DiscordListener;

public class DiscordHook {
	  NekoProfiles plugin;
	  
	  DiscordListener discord;
	  
	  public DiscordHook(NekoProfiles plugin) {
	    this.plugin = plugin;
	  }
	  
	  public void loadDiscordHook() {
	    this.discord = new DiscordListener();
	    DiscordSRV.api.subscribe(this.discord);
	    //DiscordManager.reload();
	  }
	  
	  public void unloadDiscordHook() {
	    DiscordSRV.api.unsubscribe(this.discord);
	  }
}
